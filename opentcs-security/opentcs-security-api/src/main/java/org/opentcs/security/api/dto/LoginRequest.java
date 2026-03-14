package org.opentcs.security.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求
 */
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String captchaCode;

    private String captchaId;

    private String tenantId;

    private String clientId;

    private String grantType;  // password, sms, social

    private String code;  // 短信验证码或社交授权码

    private String state;  // 社交登录 state

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCaptchaCode() { return captchaCode; }
    public void setCaptchaCode(String captchaCode) { this.captchaCode = captchaCode; }
    public String getCaptchaId() { return captchaId; }
    public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getGrantType() { return grantType; }
    public void setGrantType(String grantType) { this.grantType = grantType; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
