package org.opentcs.common.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.core.exception.ServiceException;
import org.opentcs.common.core.utils.ObjectUtils;
import org.opentcs.common.mybatis.core.domain.BaseEntity;
import org.opentcs.common.mybatis.core.domain.BusinessEntity;
import org.opentcs.common.core.spi.CurrentUserProvider;

import java.util.Date;

/**
 * MP注入处理器
 * <p>
 * 通过 {@link CurrentUserProvider} SPI 获取当前用户，
 * 不直接依赖 common-satoken，保证持久化层与认证层解耦。
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    /**
     * 如果用户不存在默认注入 -1 代表无用户
     */
    private static final Long DEFAULT_USER_ID = -1L;

    private final CurrentUserProvider currentUserProvider;

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = ObjectUtils.notNull(baseEntity.getCreateTime(), new Date());
                baseEntity.setCreateTime(current);
                baseEntity.setUpdateTime(current);

                if (ObjectUtil.isNull(baseEntity.getCreateBy())) {
                    Long userId = safeGetUserId();
                    Long deptId = safeGetDeptId();
                    baseEntity.setCreateBy(userId);
                    baseEntity.setUpdateBy(userId);
                    baseEntity.setCreateDept(ObjectUtils.notNull(baseEntity.getCreateDept(), deptId));
                }
            } else if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BusinessEntity businessEntity) {
                Date current = ObjectUtils.notNull(businessEntity.getCreateTime(), new Date());
                businessEntity.setCreateTime(current);
                businessEntity.setUpdateTime(current);

                if (ObjectUtil.isNull(businessEntity.getCreateBy())) {
                    Long userId = safeGetUserId();
                    Long deptId = safeGetDeptId();
                    businessEntity.setCreateBy(userId);
                    businessEntity.setUpdateBy(userId);
                    businessEntity.setCreateDept(ObjectUtils.notNull(businessEntity.getCreateDept(), deptId));
                }

                if (ObjectUtil.isNull(businessEntity.getDelFlag())) {
                    businessEntity.setDelFlag("0");
                }
            } else {
                Date date = new Date();
                this.strictInsertFill(metaObject, "createTime", Date.class, date);
                this.strictInsertFill(metaObject, "updateTime", Date.class, date);
                this.strictInsertFill(metaObject, "delFlag", String.class, "0");
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            Date current = new Date();
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                baseEntity.setUpdateTime(current);
                baseEntity.setUpdateBy(safeGetUserId());
            } else if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BusinessEntity businessEntity) {
                businessEntity.setUpdateTime(current);
                businessEntity.setUpdateBy(safeGetUserId());
            } else {
                this.strictUpdateFill(metaObject, "updateTime", Date.class, current);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    private Long safeGetUserId() {
        try {
            Long userId = currentUserProvider.getCurrentUserId();
            return userId != null ? userId : DEFAULT_USER_ID;
        } catch (Exception e) {
            return DEFAULT_USER_ID;
        }
    }

    private Long safeGetDeptId() {
        try {
            LoginUser loginUser = currentUserProvider.getCurrentUser();
            return loginUser != null ? loginUser.getDeptId() : DEFAULT_USER_ID;
        } catch (Exception e) {
            return DEFAULT_USER_ID;
        }
    }
}
