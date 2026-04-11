package org.opentcs.system.auth.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.core.constant.Constants;
import org.opentcs.common.core.constant.GlobalConstants;
import org.opentcs.common.core.constant.SystemConstants;
import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.core.domain.model.PasswordLoginBody;
import org.opentcs.common.core.enums.LoginType;
import org.opentcs.common.core.exception.user.CaptchaException;
import org.opentcs.common.core.exception.user.CaptchaExpireException;
import org.opentcs.common.core.exception.user.UserException;
import org.opentcs.common.core.utils.MessageUtils;
import org.opentcs.common.core.utils.StringUtils;
import org.opentcs.common.core.utils.ValidatorUtils;
import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.common.redis.utils.RedisUtils;
import org.opentcs.common.web.config.properties.CaptchaProperties;
import org.opentcs.security.api.AuthApi;
import org.opentcs.security.api.dto.TokenConfig;
import org.opentcs.security.api.dto.TokenInfo;
import org.opentcs.system.auth.IAuthStrategy;
import org.opentcs.system.auth.SysLoginService;
import org.opentcs.system.auth.vo.LoginVo;
import org.opentcs.system.domain.SysUser;
import org.opentcs.system.domain.vo.SysClientVo;
import org.opentcs.system.domain.vo.SysUserVo;
import org.opentcs.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
 * 密码认证策略
 */
@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordAuthStrategy implements IAuthStrategy {

    private final CaptchaProperties captchaProperties;
    private final SysLoginService loginService;
    private final SysUserMapper userMapper;
    private final AuthApi authApi;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        PasswordLoginBody loginBody = JsonUtils.parseObject(body, PasswordLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();

        if (captchaProperties.getEnable()) {
            validateCaptcha(username, loginBody.getCode(), loginBody.getUuid());
        }
        SysUserVo user = loadUserByUsername(username);
        loginService.checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, user.getPassword()));

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

    private void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            loginService.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!StringUtils.equalsIgnoreCase(code, captcha)) {
            loginService.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    private SysUserVo loadUserByUsername(String username) {
        SysUserVo user = userMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        } else if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UserException("user.blocked", username);
        }
        return user;
    }
}
