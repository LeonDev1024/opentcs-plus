package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.PlantModel;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 地图模型 Service 接口
 */
public interface PlantModelService extends IService<PlantModel> {

    boolean createPlantModel(PlantModel plantModel);

    /**
     * 分页查询地图模型列表
     * @param plantModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PlantModel> selectPagePlantModel(PlantModel plantModel, PageQuery pageQuery);

    /**
     * 导入地图
     * @param file 地图文件
     * @return 导入结果
     */
    boolean importMap(MultipartFile file);

    /**
     * 导出地图
     * @param modelId 地图模型ID
     * @param response 响应对象
     */
    void exportMap(Long modelId, HttpServletResponse response);

    /**
     * 版本管理 - 创建新版本
     * @param modelId 地图模型ID
     * @param versionName 版本名称
     * @return 新版本ID
     */
    Long createVersion(Long modelId, String versionName);

    /**
     * 版本管理 - 获取版本历史
     * @param modelId 地图模型ID
     * @return 版本历史列表
     */
    TableDataInfo<PlantModel> getVersionHistory(Long modelId, PageQuery pageQuery);

    /**
     * 拓扑验证
     * @param modelId 地图模型ID
     * @return 验证结果
     */
    String validateTopology(Long modelId);

    /**
     * 复制地图
     * @param modelId 地图模型ID
     * @param newName 新地图名称
     * @return 新地图ID
     */
    Long copyMap(Long modelId, String newName);
}