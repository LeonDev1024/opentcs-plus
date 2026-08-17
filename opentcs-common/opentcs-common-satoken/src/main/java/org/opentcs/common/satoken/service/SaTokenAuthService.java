package org.opentcs.common.satoken.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.satoken.api.AuthApi;
import org.opentcs.common.satoken.api.dto.TokenConfig;
import org.opentcs.common.satoken.api.dto.TokenInfo;
import org.opentcs.common.satoken.utils.LoginHelper;

/**
 * 基于 Sa-Token 的 {@link AuthApi} 实现。
 */
@Slf4j
public class SaTokenAuthService implements AuthApi {

    @Override
    public TokenInfo issueToken(LoginUser loginUser, TokenConfig config) {
        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(config.getDeviceType());
        model.setTimeout(config.getTimeout());
        model.setActiveTimeout(config.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, config.getClientId());
        LoginHelper.login(loginUser, model);

        TokenInfo info = new TokenInfo();
        info.setToken(StpUtil.getTokenValue());
        info.setUserId(loginUser.getUserId());
        info.setUsername(loginUser.getUsername());
        info.setLoginTime(System.currentTimeMillis());
        info.setExpireTime(StpUtil.getTokenTimeout());
        return info;
    }

    @Override
    public void revokeToken() {
        try {
            StpUtil.logout();
        } catch (NotLoginException ignored) {
            // 未登录时登出是幂等操作，直接忽略
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Object loginId = StpUtil.getLoginIdByToken(token);
            return loginId != null && StpUtil.isLogin(loginId);
        } catch (Exception e) {
            log.debug("Token 验证异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public TokenInfo getTokenInfo(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        LoginUser loginUser = session.get(LoginHelper.LOGIN_USER_KEY, null);
        if (ObjectUtil.isNull(loginUser)) {
            return null;
        }
        TokenInfo info = new TokenInfo();
        info.setToken(token);
        info.setUserId(loginUser.getUserId());
        info.setUsername(loginUser.getUsername());
        info.setLoginTime(null);
        info.setExpireTime(StpUtil.getTokenTimeout(token));
        return info;
    }

    @Override
    public String currentToken() {
        return StpUtil.getTokenValue();
    }

    @Override
    public long currentTokenTimeout() {
        return StpUtil.getTokenTimeout();
    }
}
