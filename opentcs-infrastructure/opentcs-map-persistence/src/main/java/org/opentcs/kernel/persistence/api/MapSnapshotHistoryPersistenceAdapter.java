package org.opentcs.kernel.persistence.api;

import lombok.RequiredArgsConstructor;
import org.opentcs.kernel.api.map.MapSnapshotHistoryPort;
import org.opentcs.kernel.persistence.service.NavigationMapHistoryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapSnapshotHistoryPersistenceAdapter implements MapSnapshotHistoryPort {

    private final NavigationMapHistoryRepository navigationMapHistoryRepository;

    @Override
    public void recordSnapshot(Long navigationMapId, String mapVersion, String snapshotUrl, String changeSummary) {
        navigationMapHistoryRepository.recordSnapshot(navigationMapId, mapVersion, snapshotUrl, changeSummary);
    }
}
