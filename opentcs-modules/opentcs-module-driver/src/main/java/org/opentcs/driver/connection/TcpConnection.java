package org.opentcs.driver.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * TCP 连接实现
 */
public class TcpConnection implements Connection {
    private String host;
    private int port;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int timeout = 5000;

    public TcpConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(timeout);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    @Override
    public void disconnect() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public void send(byte[] data) throws IOException {
        if (!isConnected()) {
            throw new IOException("Connection not established");
        }
        outputStream.write(data);
        outputStream.flush();
    }

    @Override
    public byte[] receive() throws IOException {
        if (!isConnected()) {
            throw new IOException("Connection not established");
        }
        byte[] buffer = new byte[1024];
        int length = inputStream.read(buffer);
        if (length == -1) {
            throw new IOException("Connection closed by peer");
        }
        byte[] data = new byte[length];
        System.arraycopy(buffer, 0, data, 0, length);
        return data;
    }

    @Override
    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
        if (socket != null) {
            try {
                socket.setSoTimeout(timeout);
            } catch (IOException e) {
                // 忽略异常
            }
        }
    }
}
