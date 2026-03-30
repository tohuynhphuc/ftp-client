package com.phuc.ftpclient;

import java.io.IOException;
import java.util.Scanner;

import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.util.Console;

public class App {

    private static Client client;
    private static Scanner scanner;

    public static void main(String[] args) {
        client = new Client();
        Console.announce("Program started.");

        if (!connect()) {
            return;
        }
        Console.announce("Client connected.");

        if (!welcomeMessage()) {
            return;
        }

        scanner = new Scanner(System.in);
        mainLoop();
        close();

        scanner.close();
        Console.announce("Program ended.");
    }

    private static void mainLoop() {
        while (true) {
            String command = scanner.nextLine();
            if (command.equalsIgnoreCase("end")) {
                break;
            }
            try {
                client.sendMessage(command);
                Console.message("[Server] " + client.receiveMessage());
            } catch (IOException e) {
                Console.error("[Client Error] " + e.getMessage());
                break;
            } catch (ServerException e) {
                Console.warning("[Server Error] " + e.getMessage());
            }
        }
    }

    private static boolean connect() {
        try {
            client.connect();
        } catch (IOException e) {
            Console.error("[Client Error] " + e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean welcomeMessage() {
        try {
            Console.message("[Server] " + client.receiveMessage());
        } catch (IOException e) {
            Console.error("[Client Error] " + e.getMessage());
            return false;
        } catch (ServerException e) {
            Console.warning("[Server Error] " + e.getMessage());
        }
        return true;
    }

    private static boolean close() {
        try {
            client.closeConnection();
        } catch (IOException e) {
            Console.error("[Client Error] " + e.getMessage());
            return false;
        }
        return true;
    }

}
