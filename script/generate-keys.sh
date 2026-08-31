#!/bin/bash
# Generate matching RSA key pair for OpenTCS Plus encryption
# Usage: ./script/generate-keys.sh
# Output: matching keys for backend application.yml and frontend .env.production

echo "=== Generating RSA 512-bit key pair ==="

# Generate private key in PKCS#8 format
openssl genrsa 512 | openssl pkcs8 -topk8 -inform pem -outform pem -nocrypt -out /tmp/private_key.pem 2>/dev/null

# Extract private key as single-line base64 (PKCS#8 format)
PRIVATE_KEY=$(openssl pkcs8 -topk8 -inform pem -outform der -nocrypt -in /tmp/private_key.pem 2>/dev/null | base64 -w0)

# Extract public key as single-line base64 (X.509 SPKI format)
PUBLIC_KEY=$(openssl rsa -in /tmp/private_key.pem -pubout -outform der 2>/dev/null | base64 -w0)

echo ""
echo "=== Backend (application.yml) ==="
echo "api-decrypt:"
echo "  publicKey: ${PUBLIC_KEY}    # 响应加密公钥"
echo "  privateKey: ${PRIVATE_KEY}  # 请求解密私钥"
echo ""
echo "=== Frontend (.env.production) ==="
echo "VITE_APP_RSA_PUBLIC_KEY=${PUBLIC_KEY}   # 请求加密公钥（与后端 privateKey 配对）"
echo "VITE_APP_RSA_PRIVATE_KEY=${PRIVATE_KEY}  # 响应解密私钥（与后端 publicKey 配对）"

rm -f /tmp/private_key.pem
