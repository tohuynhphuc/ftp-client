package com.phuc.ftpclient.threads;

import com.phuc.ftpclient.App;
import com.phuc.ftpclient.Client;
import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.util.Console;

public abstract class SendCommandThread extends Thread {

    private final Client client;

    public SendCommandThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                sendCommands(getCommandFromUser());
            } catch (InvalidArgumentsException e) {
                e.announceError();
            } catch (ClientIOException e) {
                e.announceError();
                App.shutdown();
                break;
            }
        }
        Console.announce("Send commands thread ended.");
    }

    protected abstract String getCommandFromUser();

    private void sendCommands(String command) throws ClientIOException, InvalidArgumentsException {
        CommandHandler.getInstance().executeCommand(client, command);
    }

}
