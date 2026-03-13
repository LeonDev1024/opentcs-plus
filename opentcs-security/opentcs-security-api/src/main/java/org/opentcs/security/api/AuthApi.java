package org.opentcs.security.api;

import org.opentcs.security.api.dto.LoginRequest;
import org.opentcs.security.api.dto.LoginResponse;
import org.opentcs.security.api.dto.TokenInfo;

/**
 * 认证服务接口
 */
public interface AuthApi {

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应（Token + 用户信息）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     * @param token 当前Token
     */
    void logout(String token);

    /**
     * 刷新 Token
     * @param refreshToken 刷新Token
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 验证 Token 有效性
     * @param token 要验证的Token
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 获取 Token 信息
     * @param token Token
     * @return Token信息
     */
    TokenInfo getTokenInfo(String token);
}
