package org.opentcs.kernel.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opentcs.common.mybatis.core.domain.DataEntity;

/**
 * 区块实体（资源互斥分组）。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("tcs_block")
public class BlockEntity extends DataEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long navigationMapId;

    private Long factoryModelId;

    private String blockId;

    private String name;

    private String type;

    /** JSON 数组字符串 */
    private String members;

    private String color;

    /** JSON 对象字符串 */
    private String properties;
}
