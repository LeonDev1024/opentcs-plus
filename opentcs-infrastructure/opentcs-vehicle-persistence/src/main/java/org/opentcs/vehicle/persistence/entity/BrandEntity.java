package org.opentcs.vehicle.persistence.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 品牌数据模型
 * 配置表：brand 品牌表
 */
@Data
@TableName("brand")
public class BrandEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌缩写代码
     */
    private String code;

    /**
     * Logo URL
     */
    private String logo;

    /**
     * 官网地址
     */
    private String website;

    /**
     * 品牌描述
     */
    private String description;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志
     */
    @TableField(fill = FieldFill.INSERT)
    private String delFlag;
}
