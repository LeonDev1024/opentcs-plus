package org.opentcs.kernel.persistence.api;

import lombok.RequiredArgsConstructor;
import org.opentcs.kernel.api.map.MapSnapshotHistoryPort;
import org.opentcs.kernel.persistence.service.NavigationMapHistoryDomainService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapSnapshotHistoryPersistenceAdapter implements MapSnapshotHistoryPort {

    private final NavigationMapHistoryDomainService navigationMapHistoryDomainService;

    @Override
    public void recordSnapshot(Long navigationMapId, String mapVersion, String snapshotUrl, String changeSummary) {
        navigationMapHistoryDomainService.recordSnapshot(navigationMapId, mapVersion, snapshotUrl, changeSummary);
    }
}
