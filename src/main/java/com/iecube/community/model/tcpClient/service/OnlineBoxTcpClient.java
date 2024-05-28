package com.iecube.community.model.tcpClient.service;

import com.iecube.community.model.tcpClient.ex.CannotConnectException;

import java.io.*;
import java.net.Socket;

public class OnlineBoxTcpClient {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Thread receiveThread;
    private boolean connected;

    private MessageListener messageListener;

    public synchronized void connect(String serverHost, int serverPort) {
        try{
            socket = new Socket(serverHost, serverPort);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            startReceiveThread();
        }catch (IOException e){
            throw new CannotConnectException(e.getMessage());
        }
    }

    public synchronized void sendMessage(String message){
        if (connected) {
            writer.println(message);
            writer.flush();
        } else {
            throw new CannotConnectException("无法连接到OnlineBox");
        }
    }

    public synchronized void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void startReceiveThread() {
        receiveThread = new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    messageListener.onMessageReceived(message);
                    break;
                }
                // 关闭连接
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
    }

    public synchronized void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

