package org.opentcs.system.auth.vo;

import lombok.Data;

/**
 * 验证码信息
 */
@Data
public class CaptchaVo {

    private Boolean captchaEnabled = true;

    private String uuid;

    private String img;
}
