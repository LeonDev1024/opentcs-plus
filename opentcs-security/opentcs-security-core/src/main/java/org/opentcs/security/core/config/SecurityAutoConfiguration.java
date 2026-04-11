package org.opentcs.security.core.config;

import org.opentcs.security.core.SaTokenAuthService;
import org.opentcs.security.core.SaTokenPermissionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * opentcs-security-core 自动配置。
 *
 * <p>注册 {@link SaTokenAuthService} 和 {@link SaTokenPermissionService} 为默认实现，
 * 业务方可通过自定义 Bean 覆盖（{@code @Primary} 或 {@code @ConditionalOnMissingBean} 生效前注册）。</p>
 */
@AutoConfiguration
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SaTokenAuthService saTokenAuthService() {
        return new SaTokenAuthService();
    }

    @Bean
    @ConditionalOnMissingBean
    public SaTokenPermissionService saTokenPermissionService() {
        return new SaTokenPermissionService();
    }
}
