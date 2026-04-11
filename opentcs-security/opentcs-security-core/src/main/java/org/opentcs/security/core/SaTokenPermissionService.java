package org.opentcs.security.core;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.satoken.utils.LoginHelper;
import org.opentcs.security.api.PermissionApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 Sa-Token 会话缓存的 {@link PermissionApi} 实现。
 *
 * <p>权限数据在用户登录时已写入 {@link LoginUser#getMenuPermission()} /
 * {@link LoginUser#getRolePermission()}，并随 token 会话缓存在 Redis 中，
 * 本实现直接从缓存读取，无需额外 DB 查询。</p>
 *
 * <p>Sa-Token 的 loginId 格式为 {@code "userType:userId"}（如 {@code "sys_user:1001"}），
 * 本实现默认以 {@code sys_user} 类型构建 loginId 进行查询；
 * 若业务需支持其他 userType，可子类化并覆盖 {@link #buildLoginId(Long)}。</p>
 */
@Slf4j
@Service
public class SaTokenPermissionService implements PermissionApi {

    private static final String DEFAULT_USER_TYPE = "sys_user";

    @Override
    public boolean hasPermission(Long userId, String permission) {
        try {
            return StpUtil.hasPermission(buildLoginId(userId), permission);
        } catch (Exception e) {
            log.debug("权限检查异常 userId={} permission={}: {}", userId, permission, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasRole(Long userId, String role) {
        try {
            return StpUtil.hasRole(buildLoginId(userId), role);
        } catch (Exception e) {
            log.debug("角色检查异常 userId={} role={}: {}", userId, role, e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        LoginUser loginUser = resolveLoginUser(userId);
        if (ObjectUtil.isNull(loginUser) || CollUtil.isEmpty(loginUser.getMenuPermission())) {
            return List.of();
        }
        return new ArrayList<>(loginUser.getMenuPermission());
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        LoginUser loginUser = resolveLoginUser(userId);
        if (ObjectUtil.isNull(loginUser) || CollUtil.isEmpty(loginUser.getRolePermission())) {
            return List.of();
        }
        return new ArrayList<>(loginUser.getRolePermission());
    }

    // ── 内部辅助 ────────────────────────────────────────────────────────────

    /**
     * 构建 Sa-Token loginId。
     * 默认 userType 为 {@value DEFAULT_USER_TYPE}，子类可覆盖以支持多用户体系。
     */
    protected String buildLoginId(Long userId) {
        return DEFAULT_USER_TYPE + ":" + userId;
    }

    /**
     * 从活跃 token 会话中解析 LoginUser。
     * 若用户当前无活跃会话则返回 null。
     */
    private LoginUser resolveLoginUser(Long userId) {
        try {
            List<String> tokens = StpUtil.getTokenValueListByLoginId(buildLoginId(userId));
            if (CollUtil.isEmpty(tokens)) {
                return null;
            }
            SaSession session = StpUtil.getTokenSessionByToken(tokens.get(0));
            if (ObjectUtil.isNull(session)) {
                return null;
            }
            return session.get(LoginHelper.LOGIN_USER_KEY, null);
        } catch (Exception e) {
            log.debug("获取用户会话异常 userId={}: {}", userId, e.getMessage());
            return null;
        }
    }
}
