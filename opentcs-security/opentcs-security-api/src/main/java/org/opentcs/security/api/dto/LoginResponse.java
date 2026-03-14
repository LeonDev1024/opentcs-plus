package org.opentcs.security.api.dto;

import java.util.List;
import java.util.Map;

/**
 * 登录响应
 */
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private String tokenType = "Bearer";

    private UserInfo user;

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    /**
     * 用户信息
     */
    public static class UserInfo {
        private Long userId;
        private String username;
        private String nickName;
        private String email;
        private String phone;
        private String avatar;
        private String tenantId;
        private Long deptId;
        private String deptName;
        private List<String> roles;
        private List<String> permissions;
        private List<String> posts;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickName() { return nickName; }
        public void setNickName(String nickName) { this.nickName = nickName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public Long getDeptId() { return deptId; }
        public void setDeptId(Long deptId) { this.deptId = deptId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
        public List<String> getPosts() { return posts; }
        public void setPosts(List<String> posts) { this.posts = posts; }
    }
}
