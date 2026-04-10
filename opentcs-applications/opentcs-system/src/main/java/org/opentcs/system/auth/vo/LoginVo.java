package org.opentcs.system.auth.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 登录验证信息
 */
@Data
public class LoginVo {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expire_in")
    private Long expireIn;

    @JsonProperty("refresh_expire_in")
    private Long refreshExpireIn;

    @JsonProperty("client_id")
    private String clientId;

    private String scope;

    private String openid;
}
