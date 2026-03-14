package org.opentcs.security.api.dto;

/**
 * Token 信息
 */
public class TokenInfo {

    private String token;

    private Long userId;

    private String username;

    private String tenantId;

    private Long loginTime;

    private Long expireTime;

    private String tokenType;

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getLoginTime() { return loginTime; }
    public void setLoginTime(Long loginTime) { this.loginTime = loginTime; }
    public Long getExpireTime() { return expireTime; }
    public void setExpireTime(Long expireTime) { this.expireTime = expireTime; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}
