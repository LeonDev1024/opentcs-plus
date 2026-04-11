package org.opentcs.security.api;

import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.security.api.dto.TokenConfig;
import org.opentcs.security.api.dto.TokenInfo;

/**
 * Token 生命周期服务接口。
 *
 * <p>职责边界：负责 token 的颁发、撤销和验证，<b>不负责</b>用户身份校验
 * （用户名/密码比对、验证码校验等业务逻辑由应用层 AuthStrategy 完成）。</p>
 *
 * <p>调用方（AuthStrategy 实现类）流程：
 * <ol>
 *   <li>校验用户凭证（密码/短信码/OAuth 等）</li>
 *   <li>构建 {@link LoginUser} 对象</li>
 *   <li>调用 {@link #issueToken} 颁发 token，获取 {@link TokenInfo}</li>
 * </ol>
 * </p>
 */
public interface AuthApi {

    /**
     * 为已认证的用户颁发 token，并将 loginUser 存入会话缓存。
     *
     * @param loginUser 已填充完整的登录用户信息
     * @param config    token 颁发配置（设备类型、超时等）
     * @return token 信息
     */
    TokenInfo issueToken(LoginUser loginUser, TokenConfig config);

    /**
     * 撤销当前请求上下文中的 token（等效于当前用户登出）。
     */
    void revokeToken();

    /**
     * 验证指定 token 是否有效（未过期、未被撤销）。
     *
     * @param token 待验证的 token 字符串
     * @return 有效返回 true
     */
    boolean validateToken(String token);

    /**
     * 获取指定 token 的详细信息。
     *
     * @param token token 字符串
     * @return token 信息，token 无效时返回 null
     */
    TokenInfo getTokenInfo(String token);

    /**
     * 获取当前请求上下文中的 token 字符串。
     *
     * @return token 字符串
     */
    String currentToken();

    /**
     * 获取当前 token 的剩余有效秒数。
     *
     * @return 剩余秒数，-1 表示永不过期，-2 表示已过期/不存在
     */
    long currentTokenTimeout();
}
