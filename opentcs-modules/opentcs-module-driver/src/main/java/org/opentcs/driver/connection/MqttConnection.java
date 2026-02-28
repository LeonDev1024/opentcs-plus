package org.opentcs.driver.connection;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MQTT 连接实现
 */
public class MqttConnection implements Connection {
    private String broker;
    private String clientId;
    private String topic;
    private MqttClient client;
    private BlockingQueue<byte[]> messageQueue;
    private int timeout = 5000;

    public MqttConnection(String broker, String clientId, String topic) {
        this.broker = broker;
        this.clientId = clientId;
        this.topic = topic;
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void connect() throws IOException {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(timeout);

            client = new MqttClient(broker, clientId, new MemoryPersistence());
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // 连接丢失处理
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    messageQueue.offer(message.getPayload());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // 消息发送完成处理
                }
            });

            client.connect(options);
            client.subscribe(topic);
        } catch (MqttException e) {
            throw new IOException("Failed to connect to MQTT broker", e);
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                client.close();
            }
        } catch (MqttException e) {
            throw new IOException("Failed to disconnect from MQTT broker", e);
        }
    }

    @Override
    public void send(byte[] data) throws IOException {
        if (!isConnected()) {
            throw new IOException("Connection not established");
        }
        try {
            MqttMessage message = new MqttMessage(data);
            message.setQos(1);
            client.publish(topic, message);
        } catch (MqttException e) {
            throw new IOException("Failed to send MQTT message", e);
        }
    }

    @Override
    public byte[] receive() throws IOException {
        if (!isConnected()) {
            throw new IOException("Connection not established");
        }
        try {
            byte[] data = messageQueue.poll(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
            if (data == null) {
                throw new IOException("Receive timeout");
            }
            return data;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Receive interrupted", e);
        }
    }

    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
