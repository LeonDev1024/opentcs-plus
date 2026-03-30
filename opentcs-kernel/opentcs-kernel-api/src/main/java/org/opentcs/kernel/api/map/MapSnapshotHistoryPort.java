package org.opentcs.kernel.api.map;

/**
 * 地图快照历史记录端口。
 */
public interface MapSnapshotHistoryPort {

    void recordSnapshot(Long navigationMapId, String mapVersion, String snapshotUrl, String changeSummary);
}
