package org.opentcs.driver.connection;

import java.io.IOException;

/**
 * 连接接口
 */
public interface Connection {
    /**
     * 连接
     * @throws IOException 连接异常
     */
    void connect() throws IOException;

    /**
     * 断开连接
     * @throws IOException 断开连接异常
     */
    void disconnect() throws IOException;

    /**
     * 发送数据
     * @param data 数据
     * @throws IOException 发送异常
     */
    void send(byte[] data) throws IOException;

    /**
     * 接收数据
     * @return 数据
     * @throws IOException 接收异常
     */
    byte[] receive() throws IOException;

    /**
     * 连接状态
     * @return 是否连接
     */
    boolean isConnected();

    /**
     * 设置超时时间
     * @param timeout 超时时间（毫秒）
     */
    void setTimeout(int timeout);
}
