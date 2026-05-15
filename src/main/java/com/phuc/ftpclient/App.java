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

        try {
            client.startReceiveMessageThread();
            System.out.println("YAY");
        } catch (IOException ex) {
            System.getLogger(App.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (ServerException ex) {
            System.getLogger(App.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
            System.out.println(command);
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
