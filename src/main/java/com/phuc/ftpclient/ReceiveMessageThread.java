package com.phuc.ftpclient;

import java.io.BufferedReader;
import java.io.IOException;

import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.util.Console;

public class ReceiveMessageThread extends Thread {

    private final BufferedReader reader;

    public ReceiveMessageThread(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        String receivedMessage;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                receivedMessage = reader.readLine();
                if (receivedMessage == null) {
                    // Connection closed by server
                    Console.announce("Connection closed by server.");
                    break;
                }

                Console.announce("[SERVER] ");
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

                Console.message(receivedMessage);
            } catch (IOException ex) {
                // Thread is being shut down
                if (Thread.currentThread().isInterrupted()) {
                    Console.announce("Receive thread shutting down.");
                    break;
                }
                ClientIOException e = new ClientIOException(ex.getMessage());
                e.announceError();
                Console.error("The system will now stop receiving messages.");
                break;
            } catch (ServerException e) {
                e.announceError();
            }
        }
        Console.announce("Receive thread ended.");
    }

}
