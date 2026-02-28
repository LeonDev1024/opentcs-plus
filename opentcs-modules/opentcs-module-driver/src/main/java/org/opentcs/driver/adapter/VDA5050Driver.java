package org.opentcs.driver.adapter;

import org.opentcs.driver.core.VehicleDriver;
import org.opentcs.driver.protocol.VDA5050Message;
import org.opentcs.driver.protocol.StateMessage;
import org.opentcs.driver.protocol.OrderMessage;
import org.opentcs.driver.protocol.InstantActionsMessage;
import org.opentcs.driver.protocol.FactsheetMessage;
import org.opentcs.driver.connection.Connection;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * VDA5050 驱动实现
 */
public class VDA5050Driver implements VehicleDriver {
    private String vehicleId;
    private Connection connection;
    private boolean running;
    private ExecutorService executorService;
    private ObjectMapper objectMapper;
    private int headerIdCounter;

    public VDA5050Driver(String vehicleId, Connection connection) {
        this.vehicleId = vehicleId;
        this.connection = connection;
        this.executorService = Executors.newSingleThreadExecutor();
        this.objectMapper = new ObjectMapper();
        this.headerIdCounter = 0;
    }

    @Override
    public String getVehicleId() {
        return vehicleId;
    }

    @Override
    public void start() {
        running = true;
        executorService.submit(this::receiveMessages);
    }

    @Override
    public void stop() {
        running = false;
        executorService.shutdown();
        try {
            connection.disconnect();
        } catch (IOException e) {
            // 忽略异常
        }
    }

    @Override
    public void sendMessage(VDA5050Message message) {
        try {
            String json = message.toJson();
            connection.send(json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public void sendOrder(OrderMessage order) {
        try {
            order.setHeaderId(headerIdCounter++);
            order.setTimestamp(VDA5050Message.generateTimestamp());
            order.setVersion("2.1.0");
            order.setManufacturer("OpenTCS");
            order.setSerialNumber(vehicleId);
            String json = objectMapper.writeValueAsString(order);
            connection.send(json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to send order", e);
        }
    }

    public void sendInstantActions(InstantActionsMessage actions) {
        try {
            actions.setHeaderId(headerIdCounter++);
            actions.setTimestamp(VDA5050Message.generateTimestamp());
            actions.setVersion("2.1.0");
            actions.setManufacturer("OpenTCS");
            actions.setSerialNumber(vehicleId);
            String json = objectMapper.writeValueAsString(actions);
            connection.send(json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to send instant actions", e);
        }
    }

    @Override
    public VDA5050Message receiveMessage() {
        try {
            byte[] data = connection.receive();
            String json = new String(data);
            return VDA5050Message.fromJson(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to receive message", e);
        }
    }

    @Override
    public boolean isConnected() {
        return connection.isConnected();
    }

    @Override
    public void connect() {
        try {
            connection.connect();
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException("Failed to disconnect", e);
        }
    }

    /**
     * 接收消息的线程
     */
    private void receiveMessages() {
        while (running) {
            try {
                byte[] data = connection.receive();
                String json = new String(data);
                // 根据消息内容判断消息类型
                if (json.contains("orderId") && json.contains("batteryState")) {
                    // 状态消息
                    StateMessage stateMessage = objectMapper.readValue(json, StateMessage.class);
                    handleStateMessage(stateMessage);
                } else if (json.contains("agvType")) {
                    // 事实表消息
                    FactsheetMessage factsheetMessage = objectMapper.readValue(json, FactsheetMessage.class);
                    handleFactsheetMessage(factsheetMessage);
                } else if (json.contains("actions") && !json.contains("nodes")) {
                    // 即时动作响应消息
                    InstantActionsMessage actionsMessage = objectMapper.readValue(json, InstantActionsMessage.class);
                    handleInstantActionsMessage(actionsMessage);
                } else {
                    // 其他消息类型
                    VDA5050Message message = VDA5050Message.fromJson(json);
                    handleMessage(message);
                }
            } catch (Exception e) {
                // 记录异常，继续接收
                System.err.println("Error receiving message: " + e.getMessage());
            }
        }
    }

    /**
     * 处理即时动作消息
     * @param actionsMessage 即时动作消息
     */
    private void handleInstantActionsMessage(InstantActionsMessage actionsMessage) {
        // 处理即时动作消息
        System.out.println("Received instant actions message: " + actionsMessage.getActions().size() + " actions");
        // 这里可以处理即时动作的执行状态等
    }

    /**
     * 处理接收到的消息
     * @param message 消息
     */
    private void handleMessage(VDA5050Message message) {
        // 这里可以根据消息类型进行不同的处理
        // 例如：处理状态消息、错误消息等
        System.out.println("Received message: " + message.toJson());
    }

    /**
     * 处理状态消息
     * @param stateMessage 状态消息
     */
    private void handleStateMessage(StateMessage stateMessage) {
        // 处理状态消息
        System.out.println("Received state message: " + stateMessage.getOrderId());
        
        // 处理错误信息
        if (stateMessage.getErrors() != null && !stateMessage.getErrors().isEmpty()) {
            for (StateMessage.Error error : stateMessage.getErrors()) {
                System.err.println("AGV Error: " + error.getErrorType() + " - " + error.getDescription() + " (Level: " + error.getErrorLevel() + ")");
                // 这里可以将错误信息存储到数据库或发送到监控系统
            }
        }
        
        // 这里可以更新车辆状态等
    }

    /**
     * 处理事实表消息
     * @param factsheetMessage 事实表消息
     */
    private void handleFactsheetMessage(FactsheetMessage factsheetMessage) {
        // 处理事实表消息
        System.out.println("Received factsheet message: " + factsheetMessage.getAgvType());
        // 这里可以存储AGV的能力信息等
    }
}
