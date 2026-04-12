package org.opentcs.common.core.spi;

import org.opentcs.common.core.domain.model.LoginUser;

/**
 * 当前用户信息提供者接口。
 * <p>
 * 定义于 common-core，由认证实现模块（如 common-satoken）提供 Bean，
 * 由持久化模块（common-mybatis）消费，从而解除两者之间的直接依赖。
 * </p>
 */
public interface CurrentUserProvider {

    /** 获取当前登录用户 ID，未登录时返回 null。 */
    Long getCurrentUserId();

    /** 获取当前登录用户所属部门 ID，未登录时返回 null。 */
    Long getCurrentDeptId();

    /** 获取当前登录用户完整信息，未登录时返回 null。 */
    LoginUser getCurrentUser();

    /** 判断当前用户是否为超级管理员。 */
    boolean isSuperAdmin();
}
