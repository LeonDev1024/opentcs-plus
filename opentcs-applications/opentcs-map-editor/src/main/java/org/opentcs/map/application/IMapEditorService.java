package org.opentcs.map.application;

import jakarta.servlet.http.HttpServletResponse;
import org.opentcs.map.domain.bo.MapEditorBO;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 地图编辑器应用服务接口
 */
public interface IMapEditorService {

    /**
     * 加载地图模型
     * @param loadModelVO 加载模型参数
     * @return 地图模型BO
     */
    MapEditorBO load(LoadModelVO loadModelVO);

    /**
     * 保存地图模型
     * @param mapEditorBO 地图模型BO
     * @return 是否保存成功
     */
    Boolean save(MapEditorBO mapEditorBO);

    /**
     * 导入地图
     * @param file 地图文件
     * @return 导入结果
     */
    MapEditorBO importMap(MultipartFile file);

    /**
     * 导出地图
     * @param id 地图模型ID
     * @param response 响应
     */
    void exportMap(Long id, HttpServletResponse response);

    /**
     * 上传编辑器数据
     * @param id 地图模型ID
     * @param file 编辑器数据文件
     * @return 是否成功
     */
    Boolean uploadEditorData(Long id, MultipartFile file);
}
