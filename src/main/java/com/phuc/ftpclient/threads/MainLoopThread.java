package com.phuc.ftpclient.threads;

import java.util.Scanner;

import com.phuc.ftpclient.Client;
import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.util.Console;

public class MainLoopThread extends Thread {

    private final Scanner scanner;
    private final CommandHandler commandHandler;
    private final Client client;

    public MainLoopThread(Scanner scanner, CommandHandler commandHandler, Client client) {
        this.scanner = scanner;
        this.commandHandler = commandHandler;
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String command = scanner.nextLine();
                if (command.equalsIgnoreCase("end")) {
                    client.closeConnection();
                    break;
                }

                commandHandler.executeCommand(client, command);
            } catch (InvalidArgumentsException e) {
                e.announceError();
            } catch (ClientIOException ex) {
                System.getLogger(MainLoopThread.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                break;
            }
        }
        Console.announce("Main loop thread ended.");
    }

}
