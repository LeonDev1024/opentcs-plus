package org.opentcs.common.satoken.api;

import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.satoken.api.dto.TokenConfig;
import org.opentcs.common.satoken.api.dto.TokenInfo;

/**
 * Token 生命周期服务接口。
 *
 * <p>职责边界：负责 token 的颁发、撤销和验证，<b>不负责</b>用户身份校验
 * （用户名/密码比对、验证码校验等业务逻辑由应用层 AuthStrategy 完成）。</p>
 */
public interface AuthApi {

    TokenInfo issueToken(LoginUser loginUser, TokenConfig config);

    void revokeToken();

    boolean validateToken(String token);

    TokenInfo getTokenInfo(String token);

    String currentToken();

    long currentTokenTimeout();
}
