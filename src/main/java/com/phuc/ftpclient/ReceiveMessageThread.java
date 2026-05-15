package com.phuc.ftpclient;

import java.io.BufferedReader;
import java.io.IOException;

import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.util.Console;

public class ReceiveMessageThread extends Thread {

    private final BufferedReader reader;

    public ReceiveMessageThread(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        Console.warning("HERE in Thread");
        String receivedMessage = "";
        while (true) {
            Console.warning("HERE in while loop");
            try {
                receivedMessage = reader.readLine();
                Console.warning("Received a message");
                int messageCode = Integer.parseInt(receivedMessage.substring(0, 3));

                if (messageCode >= 400 || messageCode < 100) {
                    throw new ServerException(receivedMessage);
                }

                boolean isMultiLine = receivedMessage.charAt(3) == Constants.MESSAGE_DELIMITER;
                boolean isEndOfMessage = false;

                while (isMultiLine && !isEndOfMessage) {
                    String nextLine = reader.readLine();
                    receivedMessage += "\n" + nextLine;
                    isEndOfMessage = nextLine.substring(0, 4)
                            .equalsIgnoreCase(String.valueOf(messageCode) + Constants.END_OF_MESSAGE_DELIMITER);
                }
            } catch (IOException e) {
                continue;
            } catch (ServerException e) {
                Console.warning("[Server Error] " + e.getMessage());
            }
            Console.message(receivedMessage);
        }

    }

}
