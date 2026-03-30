package com.phuc.ftpclient;

import java.io.IOException;
import java.util.Scanner;

import com.phuc.ftpclient.exception.ServerException;

public class App {

    public static void main(String[] args) {
        Client client = new Client();

        System.out.println(Constants.CYAN + "Program Started." + Constants.RESET);
        try {
            client.connect();
        } catch (IOException e) {
            System.err.println(Constants.RED + "[Client Error] " + e.getMessage() + Constants.RESET);
            return;
        }

        System.out.println(Constants.CYAN + "Client connected." + Constants.RESET);

        try {
            System.out.println("[Server] " + client.receiveMessage());
        } catch (IOException e) {
            System.err.println(Constants.RED + "[Client Error] " + e.getMessage() + Constants.RESET);
            return;
        } catch (ServerException e) {
            System.err.println(Constants.PURPLE + "[Server Error] " + e.getMessage() + Constants.RESET);
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine();
            if (command.equalsIgnoreCase("end")) {
                break;
            }
            try {
                client.sendMessage(command);
                System.out.println("[Server] " + client.receiveMessage());
            } catch (IOException e) {
                System.err.println(Constants.RED + "[Client Error] " + e.getMessage() + Constants.RESET);
                break;
            } catch (ServerException e) {
                System.err.println(Constants.PURPLE + "[Server Error] " + e.getMessage() + Constants.RESET);
            }
        }

        try {
            client.closeConnection();
        } catch (IOException e) {
            System.err.println(Constants.RED + "[Client Error] " + e.getMessage() + Constants.RESET);
            return;
        }

        System.out.println(Constants.CYAN + "Program ended." + Constants.RESET);
    }

}
