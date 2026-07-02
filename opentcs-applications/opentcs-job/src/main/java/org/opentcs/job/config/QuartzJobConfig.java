package org.opentcs.job.config;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * 使 Quartz Job 实例支持 Spring 依赖注入（AbstractQuartzJob 中的 Service 等）。
 */
@Configuration
public class QuartzJobConfig {

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(ApplicationContext applicationContext) {
        return schedulerFactoryBean -> {
            SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
            jobFactory.setApplicationContext(applicationContext);
            schedulerFactoryBean.setJobFactory(jobFactory);
        };
    }
}
