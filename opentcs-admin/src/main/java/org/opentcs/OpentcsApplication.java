package org.opentcs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 *
 * @author Lion Li
 */

@SpringBootApplication
@EnableScheduling
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
        "org.opentcs.job",
        "org.opentcs.common",
        "org.opentcs.security",
        "org.opentcs.algorithm",     // 算法插件 + AutoConfiguration
        "org.opentcs.strategies",   // 内置策略
        "org.opentcs.simulation"    // 仿真引擎
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