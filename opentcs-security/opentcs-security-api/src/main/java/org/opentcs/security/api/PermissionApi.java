package org.opentcs.security.api;

import java.util.List;

/**
 * 权限查询服务接口。
 *
 * <p>基于已登录用户的会话缓存（Sa-Token session）查询权限和角色。
 * 如果用户当前无活跃会话，{@link #getUserPermissions} / {@link #getUserRoles}
 * 返回空列表。</p>
 *
 * <p>需要查询菜单树或数据权限范围（deptScope）等需要 DB 访问的操作，
 * 属于应用层职责，应由对应的业务服务（如 ISysMenuService）直接提供。</p>
 */
public interface PermissionApi {

    /**
     * 检查指定用户是否拥有某个权限标识。
     *
     * @param userId     用户 ID
     * @param permission 权限标识，如 {@code "system:user:list"}
     * @return 有权限返回 true
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 检查指定用户是否拥有某个角色。
     *
     * @param userId 用户 ID
     * @param role   角色标识，如 {@code "admin"}
     * @return 有角色返回 true
     */
    boolean hasRole(Long userId, String role);

    /**
     * 获取指定用户所有权限标识列表（来自会话缓存）。
     *
     * @param userId 用户 ID
     * @return 权限标识列表；用户无活跃会话时返回空列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取指定用户所有角色标识列表（来自会话缓存）。
     *
     * @param userId 用户 ID
     * @return 角色标识列表；用户无活跃会话时返回空列表
     */
    List<String> getUserRoles(Long userId);
}
