package com.phuc.ftpclient;

import java.util.Scanner;

import com.phuc.ftpclient.exception.ClientIOException;
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

            scanner = new Scanner(System.in);
        } catch (ClientIOException e) {
            Console.error("Client IO Exception called");
            e.announceError();

            shutdown();
        } finally {
            cleanup();
        }
    }

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
