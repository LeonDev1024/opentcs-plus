package org.opentcs.simulation.core;

/**
 * 仿真模块接口
 */
public interface SimulationModule {
    
    /**
     * 初始化模块
     */
    void initialize();
    
    /**
     * 启动模块
     */
    void start();
    
    /**
     * 停止模块
     */
    void stop();
    
    /**
     * 仿真 tick
     * @param tick 当前仿真 tick
     */
    void tick(long tick);
    
    /**
     * 获取模块名称
     * @return 模块名称
     */
    String getName();
}