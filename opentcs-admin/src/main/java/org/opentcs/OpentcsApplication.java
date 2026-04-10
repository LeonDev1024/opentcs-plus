package org.opentcs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 启动程序
 *
 * @author Lion Li
 */

@SpringBootApplication
@MapperScan("org.opentcs.**.mapper")
@ComponentScan(
    basePackages = {
        "org.opentcs.web",
        "org.opentcs.kernel",
        "org.opentcs.driver",
        "org.opentcs.map",
        "org.opentcs.order",
        "org.opentcs.vehicle",
        "org.opentcs.system",
        "org.opentcs.common",
        "org.opentcs.security"
        // org.opentcs.strategies: 算法插件通过 BuiltinStrategiesAutoConfiguration 显式注册，无需组件扫描
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.opentcs\\.common\\.tenant\\..*")
    }
)
public class OpentcsApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(OpentcsApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  OpentcsApplication启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}