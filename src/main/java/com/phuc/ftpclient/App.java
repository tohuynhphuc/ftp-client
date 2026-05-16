package com.phuc.ftpclient;

import java.util.Scanner;

import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.util.Console;

public class App {

    private static Client client;
    private static Scanner scanner;
    private static final CommandHandler commandHandler = new CommandHandler();

    private static volatile boolean isRunning = false;
    private static Thread receiveThread;
    private static Thread mainThread;

    public static void main(String[] args) {
        try {
            client = new Client();
            Console.announce("Program started.");
            isRunning = true;

            connect();
            Console.announce("Client connected.");

            Thread.UncaughtExceptionHandler h = (thread, exception) -> {
                Console.announce("Exception in thread: " + thread.getName());
                shutdown();
            };

            receiveThread = client.startReceiveMessageThread(h);

            scanner = new Scanner(System.in);
            startMainLoopThread(h);

            receiveThread.join();
            mainThread.join();
        } catch (ClientIOException e) {
            Console.error("Client IO Exception called");
            e.announceError();

            shutdown();
        } catch (InterruptedException ex) {
            // TODO: Custom Exception
            Console.error("Interrupted Exception called");
            shutdown();
        } finally {
            cleanup();
        }
    }

    private static void startMainLoopThread(Thread.UncaughtExceptionHandler h) throws ClientIOException {
        mainThread = new MainLoopThread(scanner, commandHandler, client);

        mainThread.start();
    }

    private static void connect() throws ClientIOException {
        client.connect();
    }

    private static synchronized void shutdown() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        Console.announce("Initiating graceful shutdown...");

        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
        }

        if (mainThread != null && mainThread.isAlive()) {
            mainThread.interrupt();
        }

        try {
            if (receiveThread != null && receiveThread.isAlive()) {
                receiveThread.join(5000);
            }
            if (mainThread != null && mainThread.isAlive()) {
                mainThread.join(5000);
            }
        } catch (InterruptedException ex) {
            // TODO: Custom Exception
            Console.error("Interrupted Exception called");
        }
    }

    private static void cleanup() {
        try {
            close();
        } catch (ClientIOException e) {
            e.announceError();
        }

        if (scanner != null) {
            scanner.close();
        }

        Console.announce("Program ended.");
    }

    private static void close() throws ClientIOException {
        client.closeConnection();
    }

}
