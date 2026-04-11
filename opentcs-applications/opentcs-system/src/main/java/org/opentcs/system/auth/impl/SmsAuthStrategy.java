package org.opentcs.system.auth.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.core.constant.Constants;
import org.opentcs.common.core.constant.GlobalConstants;
import org.opentcs.common.core.constant.SystemConstants;
import org.opentcs.common.core.domain.model.LoginUser;
import org.opentcs.common.core.domain.model.SmsLoginBody;
import org.opentcs.common.core.enums.LoginType;
import org.opentcs.common.core.exception.user.CaptchaExpireException;
import org.opentcs.common.core.exception.user.UserException;
import org.opentcs.common.core.utils.MessageUtils;
import org.opentcs.common.core.utils.StringUtils;
import org.opentcs.common.core.utils.ValidatorUtils;
import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.common.redis.utils.RedisUtils;
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
 * 短信认证策略
 */
@Slf4j
@Service("sms" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class SmsAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;
    private final SysUserMapper userMapper;
    private final AuthApi authApi;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        SmsLoginBody loginBody = JsonUtils.parseObject(body, SmsLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String phonenumber = loginBody.getPhonenumber();
        String smsCode = loginBody.getSmsCode();

        SysUserVo user = loadUserByPhonenumber(phonenumber);
        loginService.checkLogin(LoginType.SMS, user.getUserName(), () -> !validateSmsCode(phonenumber, smsCode));

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

    private boolean validateSmsCode(String phonenumber, String smsCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber);
        if (StringUtils.isBlank(code)) {
            loginService.recordLogininfor(phonenumber, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(smsCode);
    }

    private SysUserVo loadUserByPhonenumber(String phonenumber) {
        SysUserVo user = userMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhonenumber, phonenumber));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", phonenumber);
            throw new UserException("user.not.exists", phonenumber);
        } else if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", phonenumber);
            throw new UserException("user.blocked", phonenumber);
        }
        return user;
    }
}
