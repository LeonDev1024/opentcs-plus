package org.opentcs.security.api.dto;

/**
 * Token 颁发配置，由认证策略构建后传入 AuthApi.issueToken()
 */
public class TokenConfig {

    private String deviceType;
    private String clientId;
    private String clientKey;
    /** -1 表示使用全局配置 */
    private long timeout = -1;
    /** -1 表示使用全局配置 */
    private long activeTimeout = -1;

    private TokenConfig() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getDeviceType() { return deviceType; }
    public String getClientId()   { return clientId; }
    public String getClientKey()  { return clientKey; }
    public long   getTimeout()    { return timeout; }
    public long   getActiveTimeout() { return activeTimeout; }

    public static final class Builder {
        private final TokenConfig config = new TokenConfig();

        public Builder deviceType(String deviceType)         { config.deviceType = deviceType; return this; }
        public Builder clientId(String clientId)             { config.clientId   = clientId;   return this; }
        public Builder clientKey(String clientKey)           { config.clientKey  = clientKey;  return this; }
        public Builder timeout(long timeout)                 { config.timeout    = timeout;    return this; }
        public Builder activeTimeout(long activeTimeout)     { config.activeTimeout = activeTimeout; return this; }

        public TokenConfig build() { return config; }
    }
}
