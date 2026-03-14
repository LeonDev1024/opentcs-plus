package org.opentcs.simulation.core;

/**
 * 仿真监听器接口
 */
public interface SimulationListener {
    
    /**
     * 仿真启动时调用
     * @param engine 仿真引擎
     */
    default void onSimulationStart(SimulationEngine engine) {}
    
    /**
     * 仿真暂停时调用
     * @param engine 仿真引擎
     */
    default void onSimulationPause(SimulationEngine engine) {}
    
    /**
     * 仿真继续时调用
     * @param engine 仿真引擎
     */
    default void onSimulationResume(SimulationEngine engine) {}
    
    /**
     * 仿真停止时调用
     * @param engine 仿真引擎
     */
    default void onSimulationStop(SimulationEngine engine) {}
    
    /**
     * 仿真 tick 时调用
     * @param engine 仿真引擎
     * @param tick 当前仿真 tick
     */
    default void onSimulationTick(SimulationEngine engine, long tick) {}
}