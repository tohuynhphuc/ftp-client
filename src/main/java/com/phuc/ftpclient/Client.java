package com.phuc.ftpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.ServerException;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * Try to connect to the FTP server at the default port.
     *
     * @throws IOException
     */
    public void connect() throws ClientIOException {
        try {
            socket = new Socket(Constants.HOST_NAME, Constants.PORT);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new ClientIOException(e.getMessage());
        }
    }

    /**
     * Receive a message from the server. Currently will wait until the server
     * sends the message and will block other functions (not ideal).
     *
     * @return receivedMessage
     * @throws IOException
     * @throws ServerException
     */
    public Thread startReceiveMessageThread(Thread.UncaughtExceptionHandler h) {
        ReceiveMessageThread receiveMessageThread = new ReceiveMessageThread(reader);
        receiveMessageThread.setUncaughtExceptionHandler(h);

        receiveMessageThread.start();

        return receiveMessageThread;
    }

    public void sendMessage(String message) throws ClientIOException {
        try {
            writer.write(message + "\r\n");
            writer.flush();
            // System.out.println("[Client] Message sent: " + message);
        } catch (IOException e) {
            throw new ClientIOException(e.getMessage());
        }
    }

    /**
     * Close the established connection to the FTP server.
     *
     * @throws IOException
     */
    public void closeConnection() throws ClientIOException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new ClientIOException(e.getMessage());
        }
    }

}
