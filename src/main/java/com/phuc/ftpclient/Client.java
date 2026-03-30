package com.phuc.ftpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
    public void connect() throws IOException {
        socket = new Socket(Constants.HOST_NAME, Constants.PORT);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public String receiveMessage() throws IOException, ServerException {
        String receivedMessage = reader.readLine();

        int messageCode = Integer.parseInt(receivedMessage.substring(0, 3));

        if (messageCode >= 400 || messageCode < 100) {
            throw new ServerException(receivedMessage);
        }

        boolean isMultiLine = receivedMessage.charAt(3) == '-';
        boolean isEndOfMessage = false;

        while (isMultiLine && !isEndOfMessage) {
            String nextLine = reader.readLine();
            receivedMessage += "\n" + nextLine;
            isEndOfMessage = nextLine.substring(0, 4)
                    .equalsIgnoreCase(String.valueOf(messageCode) + " ");
        }

        return receivedMessage;
    }

    public void sendMessage(String message) throws IOException {
        writer.write(message + "\r\n");
        writer.flush();
        System.out.println("[Client] Message sent: " + message);
    }

    /**
     * Close the established connection to the FTP server.
     *
     * @throws IOException
     */
    public void closeConnection() throws IOException {
        socket.close();
    }

}
