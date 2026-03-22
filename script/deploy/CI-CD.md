# OpenTCS Plus CI/CD Pipeline

## GitHub Actions 工作流

### 1. 主 CI/CD 工作流

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  #=================== 单元测试 ===================
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: opentcs-plus-web/pnpm-lock.yaml

      - name: Cache Maven dependencies
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Cache pnpm dependencies
        uses: actions/cache@v4
        with:
          path: ~/.pnpm-store
          key: ${{ runner.os }}-pnpm-${{ hashFiles('**/pnpm-lock.yaml') }}
          restore-keys: |
            ${{ runner.os }}-pnpm-

      - name: Run Backend Tests
        run: |
          cd opentcs-plus
          mvn test -Pdev

      - name: Run Frontend Tests
        run: |
          cd opentcs-plus-web
          pnpm install --frozen-lockfile
          pnpm test

  #=================== 构建镜像 ===================
  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=ref,event=branch
            type=sha,prefix=
            type=raw,value=latest,enable={{is_default_branch}}

      # 构建后端镜像
      - name: Build Backend Image
        uses: docker/build-push-action@v5
        with:
          context: .
          file: opentcs-plus/script/deploy/backend/Dockerfile
          push: true
          tags: |
            ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/backend:${{ steps.meta.outputs.tags }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      # 构建前端镜像
      - name: Build Frontend Image
        uses: docker/build-push-action@v5
        with:
          context: ./opentcs-plus-web
          file: ../script/deploy/frontend/Dockerfile
          push: true
          tags: |
            ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/frontend:${{ steps.meta.outputs.tags }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

  #=================== 部署到服务器 ===================
  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to Server
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_KEY }}
          port: ${{ secrets.DEPLOY_PORT || '22' }}
          script: |
            cd /opt/opentcs-deploy

            # 拉取最新镜像
            docker pull ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/backend:latest
            docker pull ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/frontend:latest

            # 重新部署
            docker compose down
            docker compose up -d

            # 等待服务启动
            sleep 30

            # 健康检查
            curl -f http://localhost:8088/actuator/health
            curl -f http://localhost/health
```

### 2. 拉取请求检查工作流

```yaml
# .github/workflows/pr-check.yml
name: PR Check

on:
  pull_request:
    branches: [main]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: opentcs-plus-web/pnpm-lock.yaml

      - name: Frontend Lint
        run: |
          cd opentcs-plus-web
          pnpm install --frozen-lockfile
          pnpm lint:eslint

      - name: Frontend Build Check
        run: |
          cd opentcs-plus-web
          pnpm build:prod

  build-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Backend Build Check
        run: |
          cd opentcs-plus
          mvn clean package -DskipTests -Pprod
```

### 3. 服务器上的部署配置

```bash
# 在服务器上创建部署目录
mkdir -p /opt/opentcs-deploy
cd /opt/opentcs-deploy

# 创建 docker-compose.prod.yml
cat > docker-compose.prod.yml << 'EOF'
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    # 使用外部卷或主机路径
    volumes:
      - /data/mysql:/var/lib/mysql

  redis:
    image: redis:7-alpine
    volumes:
      - /data/redis:/data

  minio:
    image: minio/minio:latest
    volumes:
      - /data/minio:/data

  backend:
    image: ghcr.io/your-org/opentcsplus/backend:latest
    restart: always

  frontend:
    image: ghcr.io/your-org/opentcsplus/frontend:latest
    restart: always
EOF

# 创建 .env 文件
cat > .env << 'EOF'
MYSQL_ROOT_PASSWORD=your-secure-password
MYSQL_DATABASE=opentcs
MYSQL_USER=opentcs
MYSQL_PASSWORD=your-secure-password
REDIS_PASSWORD=your-secure-password
MINIO_USER=minioadmin
MINIO_PASSWORD=your-secure-password
EOF
```

## Jenkins Pipeline 示例

```groovy
// Jenkinsfile
pipeline {
    agent any

    environment {
        REGISTRY = 'registry.example.com'
        IMAGE_NAME = 'opentcsplus'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir('opentcs-plus') {
                    sh 'mvn clean package -DskipTests -Pprod'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('opentcs-plus-web') {
                    sh 'pnpm install --frozen-lockfile'
                    sh 'pnpm build:prod'
                }
            }
        }

        stage('Build Images') {
            steps {
                script {
                    def backendImage = docker.build("${REGISTRY}/${IMAGE_NAME}/backend:${env.BUILD_NUMBER}")
                    def frontendImage = docker.build("${REGISTRY}/${IMAGE_NAME}/frontend:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                sh '''
                    cd deploy
                    docker-compose -f docker-compose.yml up -d
                '''
            }
        }

        stage('Integration Tests') {
            steps {
                sh '''
                    curl -f http://staging.example.com/health || exit 1
                    curl -f http://staging.example.com:8088/actuator/health || exit 1
                '''
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                sh '''
                    # Tag images
                    docker tag ${REGISTRY}/${IMAGE_NAME}/backend:${BUILD_NUMBER} ${REGISTRY}/${IMAGE_NAME}/backend:latest
                    docker tag ${REGISTRY}/${IMAGE_NAME}/frontend:${BUILD_NUMBER} ${REGISTRY}/${IMAGE_NAME}/frontend:latest

                    # Push to registry
                    docker push ${REGISTRY}/${IMAGE_NAME}/backend:latest
                    docker push ${REGISTRY}/${IMAGE_NAME}/frontend:latest

                    # Deploy
                    ssh production-server "cd /opt/opentcs && docker-compose up -d"
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        failure {
            emailext subject: "Build Failed: ${env.JOB_NAME}",
                     body: "Check console output at ${env.BUILD_URL}",
                     to: 'team@example.com'
        }
    }
}
```
