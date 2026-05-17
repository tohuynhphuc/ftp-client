package com.phuc.ftpclient.threads;

import java.io.BufferedReader;
import java.io.IOException;

import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.util.Console;
import com.phuc.ftpclient.util.Constants;
import com.phuc.ftpclient.util.ServerResponse;

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

                // int messageCode = Integer.parseInt(receivedMessage.substring(0, 3));

                boolean isMultiLine = receivedMessage.charAt(3) == Constants.MESSAGE_DELIMITER;
                boolean isEndOfMessage = false;

                while (isMultiLine && !isEndOfMessage) {
                    String nextLine = reader.readLine();
                    receivedMessage += "\n" + nextLine;
                    isEndOfMessage = nextLine.charAt(3) == Constants.END_OF_MESSAGE_DELIMITER;
                }

                ServerResponse response = new ServerResponse(receivedMessage);

                Console.message("[SERVER] " + response);

                if (response.getMessageCode() >= 400 || response.getMessageCode() < 100) {
                    throw new ServerException(receivedMessage);
                }

                // Passive Mode
                if (response.getMessageCode() == 227) {
                    Thread passiveSocketThread = new PassiveSocketThread(response,
                            CommandHandler.getInstance().getPurpose());
                    passiveSocketThread.start();
                }
            } catch (IOException ex) {
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
