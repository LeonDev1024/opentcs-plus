package org.opentcs.common.satoken.mybatis;

import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.core.spi.CurrentUserProvider;
import org.opentcs.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Component;

/**
 * 基于 Sa-Token 的 {@link CurrentUserProvider} 实现。
 * <p>
 * 注册为 Spring Bean，由 common-mybatis 的 {@code MybatisPlusConfig} 自动注入，
 * 从而解除 common-mybatis 对 common-satoken 的直接编译依赖。
 * </p>
 */
@Component
public class SaTokenCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Long getCurrentUserId() {
        try {
            return LoginHelper.getUserId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Long getCurrentDeptId() {
        try {
            LoginUser user = LoginHelper.getLoginUser();
            return user != null ? user.getDeptId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public LoginUser getCurrentUser() {
        try {
            return LoginHelper.getLoginUser();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isSuperAdmin() {
        try {
            return LoginHelper.isSuperAdmin();
        } catch (Exception e) {
            return false;
        }
    }
}
