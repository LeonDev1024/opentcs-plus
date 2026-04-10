package org.opentcs.algorithm.grpc;

import io.grpc.StatusRuntimeException;
import org.opentcs.algorithm.grpc.config.GrpcAlgorithmConfiguration;
import org.opentcs.algorithm.grpc.proto.FindRouteRequest;
import org.opentcs.algorithm.grpc.proto.FindRouteResponse;
import org.opentcs.algorithm.grpc.proto.GrpcPath;
import org.opentcs.algorithm.grpc.proto.GrpcPoint;
import org.opentcs.algorithm.grpc.proto.RoutingAlgorithmServiceGrpc;
import org.opentcs.algorithm.spi.AlgorithmMeta;
import org.opentcs.algorithm.spi.RoutingAlgorithmPlugin;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * gRPC 路径规划算法桥接插件。
 * <p>
 * 将路由请求代理到外部算法服务（C++/Python/Go 实现），
 * 通过 gRPC 与远端进程通信，返回结果后映射为领域 Point 列表。
 * </p>
 *
 * <p>外部服务需实现 {@code routing_algorithm.proto} 中定义的
 * {@code RoutingAlgorithmService}。</p>
 *
 * <p>配置示例：</p>
 * <pre>
 * opentcs:
 *   algorithm:
 *     routing:
 *       provider: grpc-cpp
 * grpc:
 *   client:
 *     routing-algorithm:
 *       address: static://localhost:50051
 *       negotiation-type: PLAINTEXT
 * </pre>
 */
@AlgorithmMeta(
        name = "grpc-cpp",
        version = "1.0.0",
        description = "gRPC 远程算法桥接（支持 C++/Python/Go 等外部实现）",
        author = "OpenTCS Plus"
)
public class GrpcRoutingAlgorithmPlugin implements RoutingAlgorithmPlugin {

    private static final Logger log = LoggerFactory.getLogger(GrpcRoutingAlgorithmPlugin.class);

    private final RoutingAlgorithmServiceGrpc.RoutingAlgorithmServiceBlockingStub stub;
    private final GrpcAlgorithmConfiguration config;

    public GrpcRoutingAlgorithmPlugin(
            RoutingAlgorithmServiceGrpc.RoutingAlgorithmServiceBlockingStub stub,
            GrpcAlgorithmConfiguration config) {
        this.stub = stub;
        this.config = config;
    }

    @Override
    public List<Point> findRoute(Map<String, Point> points, Map<String, Path> paths,
                                 Point start, Point end) {
        if (start == null || end == null) {
            return Collections.emptyList();
        }

        FindRouteRequest request = buildRequest(points, paths, start, end);

        try {
            FindRouteResponse response = stub.findRoute(request);

            if (!response.getSuccess()) {
                log.warn("gRPC 算法服务返回失败: {}", response.getErrorMessage());
                return Collections.emptyList();
            }

            return response.getPointIdsList().stream()
                    .map(points::get)
                    .filter(p -> p != null)
                    .collect(Collectors.toList());

        } catch (StatusRuntimeException e) {
            log.error("gRPC 算法服务调用失败 [{}->{}]: {} {}",
                    start.getPointId(), end.getPointId(), e.getStatus().getCode(), e.getMessage());
            return Collections.emptyList();
        }
    }

    private FindRouteRequest buildRequest(Map<String, Point> points, Map<String, Path> paths,
                                          Point start, Point end) {
        List<GrpcPoint> grpcPoints = points.values().stream()
                .map(this::toGrpcPoint)
                .collect(Collectors.toList());

        List<GrpcPath> grpcPaths = paths.values().stream()
                .map(this::toGrpcPath)
                .collect(Collectors.toList());

        return FindRouteRequest.newBuilder()
                .addAllPoints(grpcPoints)
                .addAllPaths(grpcPaths)
                .setStartPointId(start.getPointId())
                .setEndPointId(end.getPointId())
                .build();
    }

    private GrpcPoint toGrpcPoint(Point p) {
        return GrpcPoint.newBuilder()
                .setPointId(p.getPointId())
                .setX(p.getX())
                .setY(p.getY())
                .build();
    }

    private GrpcPath toGrpcPath(Path p) {
        return GrpcPath.newBuilder()
                .setPathId(p.getPathId())
                .setSourcePointId(p.getSourcePointId())
                .setDestPointId(p.getDestPointId())
                .setLength(p.getLength())
                .setTraversable(p.isTraversable())
                .build();
    }
}
