package org.opentcs.system.auth.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.opentcs.common.core.constant.SystemConstants;
import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.core.domain.model.SocialLoginBody;
import org.opentcs.common.core.exception.ServiceException;
import org.opentcs.common.core.exception.user.UserException;
import org.opentcs.common.core.utils.ValidatorUtils;
import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.common.social.config.properties.SocialProperties;
import org.opentcs.common.social.utils.SocialUtils;
import org.opentcs.security.api.AuthApi;
import org.opentcs.security.api.dto.TokenConfig;
import org.opentcs.security.api.dto.TokenInfo;
import org.opentcs.system.auth.IAuthStrategy;
import org.opentcs.system.auth.SysLoginService;
import org.opentcs.system.auth.vo.LoginVo;
import org.opentcs.system.domain.vo.SysClientVo;
import org.opentcs.system.domain.vo.SysSocialVo;
import org.opentcs.system.domain.vo.SysUserVo;
import org.opentcs.system.mapper.SysUserMapper;
import org.opentcs.system.service.ISysSocialService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 第三方授权策略
 */
@Slf4j
@Service("social" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class SocialAuthStrategy implements IAuthStrategy {

    private final SocialProperties socialProperties;
    private final ISysSocialService sysSocialService;
    private final SysUserMapper userMapper;
    private final SysLoginService loginService;
    private final AuthApi authApi;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        SocialLoginBody loginBody = JsonUtils.parseObject(body, SocialLoginBody.class);
        ValidatorUtils.validate(loginBody);

        AuthResponse<AuthUser> response = SocialUtils.loginAuth(
            loginBody.getSource(), loginBody.getSocialCode(),
            loginBody.getSocialState(), socialProperties);
        if (!response.ok()) {
            throw new ServiceException(response.getMsg());
        }
        AuthUser authUserData = response.getData();

        List<SysSocialVo> list = sysSocialService.selectByAuthId(authUserData.getSource() + authUserData.getUuid());
        if (CollUtil.isEmpty(list)) {
            throw new ServiceException("你还没有绑定第三方账号，绑定后才可以登录！");
        }
        SysUserVo user = loadUser(list.get(0).getUserId());

        LoginUser loginUser = loginService.buildLoginUser(user);
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());

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
        return loginVo;
    }

    private SysUserVo loadUser(Long userId) {
        SysUserVo user = userMapper.selectVoById(userId);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户不存在, userId={}", userId);
            throw new UserException("user.not.exists", "");
        } else if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户已被停用, userId={}", userId);
            throw new UserException("user.blocked", "");
        }
        return user;
    }
}
