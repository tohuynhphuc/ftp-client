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
            // receiveThread.setDaemon(false);
            Console.announce("Receive Thread Started");

            scanner = new Scanner(System.in);
            startMainLoopThread(h);
            // mainThread.setDaemon(false);
            Console.announce("Main Thread Started");

            Console.announce("BEFORE THREADS ARE JOINED?");
            receiveThread.join();
            mainThread.join();
            Console.announce("I GOT HERE THREADS ARE JOINED?");
        } catch (ClientIOException e) {
            // INFO: These errors being catched will stop the program!
            Console.error("Client IO Exception called");
            e.announceError();

            shutdown();
        } catch (InterruptedException ex) {
            // TODO: Custom Exception
            Console.error("Interrupted Exception called");
            ex.printStackTrace();
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

        // Interrupt threads
        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
        }

        if (mainThread != null && mainThread.isAlive()) {
            mainThread.interrupt();
        }

        // Wait for threads to finish
        try {
            if (receiveThread != null && receiveThread.isAlive()) {
                receiveThread.join(5000); // Wait up to 5 seconds
            }
            if (mainThread != null && mainThread.isAlive()) {
                mainThread.join(5000); // Wait up to 5 seconds
            }
        } catch (InterruptedException ex) {
            System.getLogger(App.class.getName()).log(System.Logger.Level.WARNING, "Shutdown interrupted", ex);
        }
    }

    private static void cleanup() {
        try {
            Console.warning("CLEANUP IS BEING CALLED");
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
