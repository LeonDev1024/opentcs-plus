package org.opentcs.kernel.domain.port;

/**
 * 地图运行时协作端口。
 */
public interface MapRuntimePort {

    String getActiveMapId();

    LoadedMapSummary loadPublishedMap(String mapId);

    /**
     * 已加载地图摘要。
     */
    record LoadedMapSummary(String mapId, String version, int pointCount, int pathCount) {
    }
}
