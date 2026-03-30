package org.opentcs.kernel.api.algorithm;

/**
 * 生命周期接口。
 */
public interface Lifecycle {

    /**
     * 初始化组件。
     */
    default void initialize() {}

    /**
     * 终止组件。
     */
    default void terminate() {}

    /**
     * 检查组件是否已初始化。
     *
     * @return true 如果已初始化
     */
    boolean isInitialized();
}