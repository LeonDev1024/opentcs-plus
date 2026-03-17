package org.opentcs.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.LocationTypeDTO;
import org.opentcs.kernel.persistence.entity.LocationTypeEntity;
import org.opentcs.kernel.persistence.service.LocationTypeDomainService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置类型管理
 * @author lyc
 */
@Validated
@RestController
@RequestMapping("/map/locationType")
@RequiredArgsConstructor
public class LocationTypeController extends BaseController {

    private final LocationTypeDomainService locationTypeDomainService;
    private final ObjectMapper objectMapper;

    /**
     * 查询所有位置类型
     */
    @GetMapping("/list")
    public TableDataInfo<LocationTypeDTO> list(LocationTypeEntity locationType, PageQuery pageQuery) {
        return locationTypeDomainService.selectPageDTO(locationType, pageQuery);
    }

    /**
     * 根据ID查询位置类型
     */
    @GetMapping("/{id}")
    public R<LocationTypeDTO> getLocationTypeById(@PathVariable Long id) {
        return R.ok(locationTypeDomainService.getByIdDTO(id));
    }

    /**
     * 获取所有位置类型（不分页）
     */
    @GetMapping("/all")
    public R<List<LocationTypeDTO>> getAllLocationTypes() {
        return R.ok(locationTypeDomainService.listDTO());
    }

    /**
     * 创建位置类型
     */
    @PostMapping("/create")
    public R<Boolean> createLocationType(@RequestBody LocationTypeEntity locationType) {
        return R.ok(locationTypeDomainService.save(locationType));
    }

    /**
     * 更新位置类型
     */
    @PutMapping("/update")
    public R<Boolean> updateLocationType(@RequestBody LocationTypeEntity locationType) {
        return R.ok(locationTypeDomainService.updateById(locationType));
    }

    /**
     * 删除位置类型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteLocationType(@PathVariable Long id) {
        return R.ok(locationTypeDomainService.removeById(id));
    }
}