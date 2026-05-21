package com.phuc.ftpclient.util;

import java.io.BufferedReader;
import java.io.IOException;

import com.phuc.ftpclient.App;
import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.threads.PassiveSocketThread;

public class ReceiveMessage {

    public static ServerResponse receiveMessages() throws IOException, ServerException {
        BufferedReader reader = App.getClient().getReader();
        String receivedMessage;
        receivedMessage = reader.readLine();
        if (receivedMessage == null) {
            // Connection closed by server
            Console.announce("Connection closed by server.");
            App.shutdown();
            return null;
        }

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
            PassiveSocketThread passiveSocketThread = new PassiveSocketThread(response,
                    CommandHandler.getInstance().getPurpose(), CommandHandler.getInstance().getPathToFile());
            CommandHandler.getInstance().setPasvSocketThread(passiveSocketThread);
            passiveSocketThread.start();
        }
        return response;
    }

}
