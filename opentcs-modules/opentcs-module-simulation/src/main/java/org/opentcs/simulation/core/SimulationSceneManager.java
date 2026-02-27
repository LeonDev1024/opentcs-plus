package org.opentcs.simulation.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 仿真场景管理器
 */
@Slf4j
@Data
@Component
public class SimulationSceneManager {
    
    private final List<SimulationScene> scenes = new ArrayList<>();
    private SimulationScene currentScene;
    
    /**
     * 创建新场景
     * @param name 场景名称
     * @param description 场景描述
     * @return 新场景
     */
    public SimulationScene createScene(String name, String description) {
        SimulationScene scene = new SimulationScene();
        scene.setName(name);
        scene.setDescription(description);
        scenes.add(scene);
        currentScene = scene;
        log.info("Created new simulation scene: {}", name);
        return scene;
    }
    
    /**
     * 加载场景
     * @param filePath 文件路径
     * @return 加载的场景
     */
    public SimulationScene loadScene(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            SimulationScene scene = (SimulationScene) ois.readObject();
            scenes.add(scene);
            currentScene = scene;
            log.info("Loaded simulation scene: {} from {}", scene.getName(), filePath);
            return scene;
        } catch (Exception e) {
            log.error("Failed to load simulation scene: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 保存场景
     * @param scene 场景
     * @param filePath 文件路径
     * @return 是否保存成功
     */
    public boolean saveScene(SimulationScene scene, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(scene);
            log.info("Saved simulation scene: {} to {}", scene.getName(), filePath);
            return true;
        } catch (Exception e) {
            log.error("Failed to save simulation scene: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 删除场景
     * @param scene 场景
     * @return 是否删除成功
     */
    public boolean deleteScene(SimulationScene scene) {
        boolean removed = scenes.remove(scene);
        if (removed) {
            if (currentScene == scene) {
                currentScene = null;
            }
            log.info("Deleted simulation scene: {}", scene.getName());
        }
        return removed;
    }
    
    /**
     * 获取所有场景
     * @return 场景列表
     */
    public List<SimulationScene> getScenes() {
        return new ArrayList<>(scenes);
    }
    
    /**
     * 设置当前场景
     * @param scene 场景
     */
    public void setCurrentScene(SimulationScene scene) {
        if (scenes.contains(scene)) {
            currentScene = scene;
            log.info("Set current simulation scene: {}", scene.getName());
        } else {
            log.warn("Scene not found: {}", scene.getName());
        }
    }
}