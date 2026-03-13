package org.opentcs.security.api;

import java.util.List;
import java.util.Set;

/**
 * 权限服务接口
 */
public interface PermissionApi {

    /**
     * 检查用户是否有指定权限
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 检查用户是否有指定角色
     * @param userId 用户ID
     * @param role 角色标识
     * @return 是否有角色
     */
    boolean hasRole(Long userId, String role);

    /**
     * 获取用户所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取用户所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户可访问的菜单路由
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Object> getUserMenus(Long userId);

    /**
     * 获取用户数据权限范围
     * @param userId 用户ID
     * @return 部门ID集合
     */
    Set<Long> getUserDataScopes(Long userId);
}
