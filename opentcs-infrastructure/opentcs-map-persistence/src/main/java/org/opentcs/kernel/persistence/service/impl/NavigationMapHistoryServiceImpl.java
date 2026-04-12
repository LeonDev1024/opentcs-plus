package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.kernel.persistence.entity.NavigationMapHistoryEntity;
import org.opentcs.kernel.persistence.mapper.NavigationMapHistoryMapper;
import org.opentcs.kernel.persistence.service.NavigationMapHistoryRepository;
import org.springframework.stereotype.Service;

/**
 * 导航地图历史版本领域服务实现。
 */
@Service
public class NavigationMapHistoryServiceImpl
    extends ServiceImpl<NavigationMapHistoryMapper, NavigationMapHistoryEntity>
    implements NavigationMapHistoryRepository {

    @Override
    public void recordSnapshot(Long navigationMapId, String mapVersion, String snapshotUrl, String changeSummary) {
        NavigationMapHistoryEntity history = new NavigationMapHistoryEntity();
        history.setNavigationMapId(navigationMapId);
        history.setMapVersion(mapVersion);
        history.setSnapshotUrl(snapshotUrl);
        history.setChangeSummary(changeSummary);
        this.save(history);
    }
}
