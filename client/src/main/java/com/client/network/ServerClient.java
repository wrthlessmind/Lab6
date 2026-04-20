package com.client.network;

import com.client.exceptions.NoConnectionException;
import com.shared.network.Request;
import com.shared.network.Response;

import java.io.*;
import java.net.Socket;
import java.util.ResourceBundle;

public class ServerClient {
    private static ServerClient instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ServerClient() throws NoConnectionException {
        connect();
    }

    public static synchronized ServerClient getInstance() throws NoConnectionException {
        if (instance == null) {
            instance = new ServerClient();
        }
        return instance;
    }

    private void connect() throws NoConnectionException {
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        String ip = bundle.getString("SERVER_IP").trim();
        int port = Integer.parseInt(bundle.getString("SERVER_PORT").trim());
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Подключено к серверу " + ip + ":" + port);
        } catch (IOException e) {
            throw new NoConnectionException(
                    "Не удалось подключиться к серверу " + ip + ":" + port);
        }
    }

    public Response sendRequest(Request request) {
        try {
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            instance = null;
            System.out.println("Отключено от сервера.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}