package org.opentcs.system.auth.impl;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWechatMiniProgramRequest;
import org.opentcs.common.core.constant.SystemConstants;
import org.opentcs.common.core.domain.model.XcxLoginBody;
import org.opentcs.common.core.domain.model.XcxLoginUser;
import org.opentcs.common.core.exception.ServiceException;
import org.opentcs.common.core.utils.ValidatorUtils;
import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.security.api.AuthApi;
import org.opentcs.security.api.dto.TokenConfig;
import org.opentcs.security.api.dto.TokenInfo;
import org.opentcs.system.auth.IAuthStrategy;
import org.opentcs.system.auth.SysLoginService;
import org.opentcs.system.auth.vo.LoginVo;
import org.opentcs.system.domain.vo.SysClientVo;
import org.opentcs.system.domain.vo.SysUserVo;
import org.springframework.stereotype.Service;

/**
 * 小程序认证策略
 */
@Slf4j
@Service("xcx" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class XcxAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;
    private final AuthApi authApi;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        XcxLoginBody loginBody = JsonUtils.parseObject(body, XcxLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String xcxCode = loginBody.getXcxCode();
        String appid = loginBody.getAppid();

        AuthRequest authRequest = new AuthWechatMiniProgramRequest(AuthConfig.builder()
            .clientId(appid).clientSecret("自行填写密钥 可根据不同appid填入不同密钥")
            .ignoreCheckRedirectUri(true).ignoreCheckState(true).build());
        AuthCallback authCallback = new AuthCallback();
        authCallback.setCode(xcxCode);
        AuthResponse<AuthUser> resp = authRequest.login(authCallback);

        String openid;
        if (resp.ok()) {
            AuthToken token = resp.getData().getToken();
            openid = token.getOpenId();
        } else {
            throw new ServiceException(resp.getMsg());
        }

        SysUserVo user = loadUserByOpenid(openid);

        XcxLoginUser loginUser = new XcxLoginUser();
        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        loginUser.setOpenid(openid);

        TokenConfig config = TokenConfig.builder()
            .deviceType(client.getDeviceType())
            .clientId(client.getClientId())
            .clientKey(client.getClientKey())
            .timeout(client.getTimeout())
            .activeTimeout(client.getActiveTimeout())
            .build();
        TokenInfo tokenInfo = authApi.issueToken(loginUser, config);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(tokenInfo.getToken());
        loginVo.setExpireIn(tokenInfo.getExpireTime());
        loginVo.setClientId(client.getClientId());
        loginVo.setOpenid(openid);
        return loginVo;
    }

    private SysUserVo loadUserByOpenid(String openid) {
        // 使用 openid 查询绑定用户，如未绑定用户则根据业务自行处理
        // todo 自行实现 userService.selectUserByOpenid(openid);
        SysUserVo user = new SysUserVo();
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", openid);
            // todo 用户不存在，业务逻辑自行实现
        } else if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", openid);
            // todo 用户已被停用，业务逻辑自行实现
        }
        return user;
    }
}
