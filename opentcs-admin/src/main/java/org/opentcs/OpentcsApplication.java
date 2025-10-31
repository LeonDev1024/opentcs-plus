package org.opentcs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 启动程序
 *
 * @author Lion Li
 */

@SpringBootApplication
@MapperScan("org.opentcs.**.mapper")
public class OpentcsApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(OpentcsApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  OpentcsApplication启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}