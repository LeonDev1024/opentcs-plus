package org.opentcs.web.config;

import org.opentcs.driver.config.Vda5050AdapterConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 显式装配车载协议适配器（仅 admin 引入对应 jar）。
 */
@Configuration
@Import(Vda5050AdapterConfiguration.class)
public class DriverAdaptersImportConfiguration {
}
