package com.phuc.ftpclient.threads;

import java.io.IOException;

import com.phuc.ftpclient.App;
import com.phuc.ftpclient.Client;
import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.util.Console;

public abstract class SendCommandThread extends Thread {

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
            } catch (ServerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Console.announce("Send commands thread ended.");
    }

    protected abstract String getCommandFromUser();

    private void sendCommands(String command)
            throws ClientIOException, InvalidArgumentsException, ServerException, IOException {
        CommandHandler.getInstance().executeCommand(command);
    }

}
