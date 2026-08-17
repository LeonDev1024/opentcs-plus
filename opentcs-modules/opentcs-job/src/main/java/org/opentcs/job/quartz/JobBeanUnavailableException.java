package org.opentcs.job.quartz;

/**
 * 定时任务目标 Bean 未在 Spring 容器中注册（常见于 DB 已迁移但运行包未升级）。
 */
public class JobBeanUnavailableException extends RuntimeException {

    private final String beanName;

    public JobBeanUnavailableException(String beanName) {
        super("Bean 未注册: " + beanName + "。请暂停该定时任务，或部署包含此组件的版本后重启。");
        this.beanName = beanName;
    }

    public String beanName() {
        return beanName;
    }
}
