package org.opentcs.system.auth;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.constant.Constants;
import org.opentcs.common.core.constant.GlobalConstants;
import org.opentcs.common.core.domain.model.RegisterBody;
import org.opentcs.common.core.enums.UserType;
import org.opentcs.common.core.exception.user.CaptchaException;
import org.opentcs.common.core.exception.user.CaptchaExpireException;
import org.opentcs.common.core.exception.user.UserException;
import org.opentcs.common.core.utils.MessageUtils;
import org.opentcs.common.core.utils.ServletUtils;
import org.opentcs.common.core.utils.SpringUtils;
import org.opentcs.common.core.utils.StringUtils;
import org.opentcs.common.log.event.LogininforEvent;
import org.opentcs.common.redis.utils.RedisUtils;
import org.opentcs.common.web.config.properties.CaptchaProperties;
import org.opentcs.system.domain.SysUser;
import org.opentcs.system.domain.bo.SysUserBo;
import org.opentcs.system.mapper.SysUserMapper;
import org.opentcs.system.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * 注册校验方法（从 opentcs-admin 迁移至 opentcs-system 应用层）
 */
@RequiredArgsConstructor
@Service
public class SysRegisterService {

    private final ISysUserService userService;
    private final SysUserMapper userMapper;
    private final CaptchaProperties captchaProperties;

    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        if (captchaProperties.getEnable()) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }
        SysUserBo sysUser = new SysUserBo();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(BCrypt.hashpw(password));
        sysUser.setUserType(userType);

        boolean exist = userMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, sysUser.getUserName()));
        if (exist) {
            throw new UserException("user.register.save.error", username);
        }
        if (!userService.registerUser(sysUser)) {
            throw new UserException("user.register.error");
        }
        recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }

    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!StringUtils.equalsIgnoreCase(code, captcha)) {
            recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    private void recordLogininfor(String username, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }
}
