package com.phuc.ftpclient;

import java.util.Scanner;

import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.threads.ReceiveMessageThread;
import com.phuc.ftpclient.threads.SendCmdConsoleThread;
import com.phuc.ftpclient.util.Console;
import com.phuc.ftpclient.util.Constants;

public class App {

    private static Client client;
    private static Scanner scanner;

    private static volatile boolean isRunning = false;
    private static Thread receiveThread;
    private static Thread sendThread;

    public static void main(String[] args) {
        try {
            Console.announce("Program started.");
            connect(Constants.HOST_NAME, Constants.PORT);
            Console.announce("Client connected.");

            // Thread.UncaughtExceptionHandler h = (th, exception) -> {
            // Console.announce("Exception in thread: " + th.getName());
            // shutdown();
            // };

            scanner = new Scanner(System.in);
            // startThreads();
            // startMainLoopThread(h);

            // while (true) {
            // // send command
            // try {
            // sendCommands(scanner.nextLine());
            // } catch (InvalidArgumentsException e) {
            // e.announceError();
            // // retry the loop
            // continue;
            // }
            // }

            // receiveThread.join();
            // thread.join();
        } catch (ClientIOException e) {
            Console.error("Client IO Exception called");
            e.announceError();

            shutdown();
        } finally {
            cleanup();
        }
    }

    // public static void startThreads() {
    // receiveThread = new ReceiveMessageThread(client);
    // receiveThread.setName("receiveThread");
    // receiveThread.start();

    // if (Constants.IS_CONSOLE) {
    // sendThread = new SendCmdConsoleThread(scanner);
    // sendThread.setName("sendThread");
    // sendThread.start();
    // }

    // try {
    // if (Constants.IS_CONSOLE) {
    // receiveThread.join();
    // sendThread.join();
    // }
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

    public static void connect(String hostName, int port) throws ClientIOException {
        client = new Client();
        isRunning = true;
        client.connect(hostName, port);
    }

    public static synchronized void shutdown() {
        Console.debug("Shutdown called");
        if (!isRunning) {
            return;
        }

        isRunning = false;
        Console.announce("Initiating graceful shutdown...");

        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
        }

        if (sendThread != null && sendThread.isAlive()) {
            sendThread.interrupt();
        }

        try {
            if (receiveThread != null && receiveThread.isAlive()) {
                receiveThread.join(5000);
            }
            if (sendThread != null && sendThread.isAlive()) {
                sendThread.join(5000);
            }
        } catch (InterruptedException ex) {
            // TODO: Custom Exception
            Console.error("Interrupted Exception called");
        } finally {
            cleanup();
        }
    }

    private static void cleanup() {
        Console.debug("Cleanup called");
        try {
            close();
        } catch (ClientIOException e) {
            e.announceError();
        }

        if (scanner != null) {
            Console.debug("Scanner closing");
            scanner.close();
            Console.debug("Scanner closed");
        }

        Console.announce("Program ended.");
    }

    private static void close() throws ClientIOException {
        client.closeConnection();
    }

    public static Client getClient() {
        return client;
    }

    public static boolean getIsRunning() {
        return isRunning;
    }

}
