package org.opentcs.system.auth;

import org.opentcs.common.core.exception.ServiceException;
import org.opentcs.common.core.utils.SpringUtils;
import org.opentcs.system.auth.vo.LoginVo;
import org.opentcs.system.domain.vo.SysClientVo;

/**
 * 授权策略接口。
 * <p>
 * 各认证方式（密码/短信/邮箱/第三方/小程序）的统一入口。
 * </p>
 */
public interface IAuthStrategy {

    String BASE_NAME = "AuthStrategy";

    static LoginVo login(String body, SysClientVo client, String grantType) {
        String beanName = grantType + BASE_NAME;
        if (!SpringUtils.containsBean(beanName)) {
            throw new ServiceException("授权类型不正确!");
        }
        IAuthStrategy instance = SpringUtils.getBean(beanName);
        return instance.login(body, client);
    }

    LoginVo login(String body, SysClientVo client);
}
