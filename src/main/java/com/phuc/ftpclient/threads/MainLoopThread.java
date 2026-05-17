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

    public MainLoopThread(Scanner scanner, Client client) {
        this.scanner = scanner;
        this.commandHandler = CommandHandler.getInstance();
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // INFO: Get command from user, sends to Command Handler to execute.
                String command = scanner.nextLine();
                if (command.equalsIgnoreCase("end")) {
                    client.closeConnection();
                    break;
                }

                commandHandler.executeCommand(client, command);
            } catch (InvalidArgumentsException e) {
                e.announceError();
            } catch (ClientIOException e) {
                e.announceError();
                break;
            }
        }
        Console.announce("Main loop thread ended.");
    }

}
