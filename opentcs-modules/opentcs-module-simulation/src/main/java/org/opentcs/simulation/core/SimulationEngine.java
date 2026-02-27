package org.opentcs.simulation.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 仿真引擎
 */
@Slf4j
@Data
@Component
public class SimulationEngine {
    
    private static final int DEFAULT_TICK_RATE = 10; // 默认每秒钟10个仿真 tick
    
    private SimulationStatus status = SimulationStatus.STOPPED;
    private int tickRate = DEFAULT_TICK_RATE;
    private long currentTick = 0;
    private long startTime = 0;
    private long pausedTime = 0;
    private long pausedDuration = 0;
    
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private Future<?> simulationTask;
    
    private final List<SimulationListener> listeners = new ArrayList<>();
    private final List<SimulationModule> modules = new ArrayList<>();
    
    /**
     * 启动仿真
     */
    public synchronized void start() {
        if (status == SimulationStatus.RUNNING) {
            log.warn("Simulation is already running");
            return;
        }
        
        log.info("Starting simulation...");
        status = SimulationStatus.RUNNING;
        startTime = System.currentTimeMillis() - pausedDuration;
        
        // 初始化执行器
        executorService = Executors.newCachedThreadPool();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        
        // 启动仿真任务
        simulationTask = scheduledExecutorService.scheduleAtFixedRate(this::tick, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);
        
        // 通知监听器
        notifyListeners(listener -> listener.onSimulationStart(this));
    }
    
    /**
     * 暂停仿真
     */
    public synchronized void pause() {
        if (status != SimulationStatus.RUNNING) {
            log.warn("Simulation is not running");
            return;
        }
        
        log.info("Pausing simulation...");
        status = SimulationStatus.PAUSED;
        pausedTime = System.currentTimeMillis();
        
        // 取消仿真任务
        if (simulationTask != null) {
            simulationTask.cancel(false);
        }
        
        // 通知监听器
        notifyListeners(listener -> listener.onSimulationPause(this));
    }
    
    /**
     * 继续仿真
     */
    public synchronized void resume() {
        if (status != SimulationStatus.PAUSED) {
            log.warn("Simulation is not paused");
            return;
        }
        
        log.info("Resuming simulation...");
        status = SimulationStatus.RUNNING;
        pausedDuration += System.currentTimeMillis() - pausedTime;
        
        // 重新启动仿真任务
        simulationTask = scheduledExecutorService.scheduleAtFixedRate(this::tick, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);
        
        // 通知监听器
        notifyListeners(listener -> listener.onSimulationResume(this));
    }
    
    /**
     * 停止仿真
     */
    public synchronized void stop() {
        if (status == SimulationStatus.STOPPED) {
            log.warn("Simulation is already stopped");
            return;
        }
        
        log.info("Stopping simulation...");
        status = SimulationStatus.STOPPED;
        
        // 取消仿真任务
        if (simulationTask != null) {
            simulationTask.cancel(false);
        }
        
        // 关闭执行器
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
        
        // 重置仿真状态
        currentTick = 0;
        startTime = 0;
        pausedTime = 0;
        pausedDuration = 0;
        
        // 通知监听器
        notifyListeners(listener -> listener.onSimulationStop(this));
    }
    
    /**
     * 仿真 tick
     */
    private void tick() {
        if (status != SimulationStatus.RUNNING) {
            return;
        }
        
        currentTick++;
        
        // 执行所有模块的 tick
        for (SimulationModule module : modules) {
            try {
                module.tick(currentTick);
            } catch (Exception e) {
                log.error("Error in simulation module tick: {}", e.getMessage(), e);
            }
        }
        
        // 通知监听器
        notifyListeners(listener -> listener.onSimulationTick(this, currentTick));
    }
    
    /**
     * 添加仿真模块
     * @param module 仿真模块
     */
    public void addModule(SimulationModule module) {
        modules.add(module);
    }
    
    /**
     * 移除仿真模块
     * @param module 仿真模块
     */
    public void removeModule(SimulationModule module) {
        modules.remove(module);
    }
    
    /**
     * 添加仿真监听器
     * @param listener 仿真监听器
     */
    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }
    
    /**
     * 移除仿真监听器
     * @param listener 仿真监听器
     */
    public void removeListener(SimulationListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * 通知所有监听器
     * @param action 通知动作
     */
    private void notifyListeners(SimulationListenerAction action) {
        for (SimulationListener listener : listeners) {
            try {
                action.execute(listener);
            } catch (Exception e) {
                log.error("Error notifying simulation listener: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * 仿真监听器动作
     */
    @FunctionalInterface
    private interface SimulationListenerAction {
        void execute(SimulationListener listener);
    }
    
    /**
     * 仿真状态
     */
    public enum SimulationStatus {
        STOPPED,
        RUNNING,
        PAUSED
    }
}