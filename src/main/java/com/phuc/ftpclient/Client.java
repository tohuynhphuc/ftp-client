package com.phuc.ftpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.util.Console;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public void connect(String hostName, int port) throws ClientIOException {
        try {
            socket = new Socket(hostName, port);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new ClientIOException(e.getMessage());
        }
    }

    public void sendMessage(String message) throws ClientIOException {
        try {
            Console.debug(message);
            writer.write(message + "\r\n");
            writer.flush();
        } catch (IOException e) {
            throw new ClientIOException(e.getMessage());
        }
    }

    public void closeConnection() throws ClientIOException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new ClientIOException(e.getMessage());
        }
    }

    public BufferedReader getReader() {
        return reader;
    }

}
