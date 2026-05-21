package com.phuc.ftpclient.threads;

import java.util.Scanner;

import com.phuc.ftpclient.Client;

public class SendCmdConsoleThread extends SendCommandThread {

    private final Scanner scanner;

    public SendCmdConsoleThread(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    protected String getCommandFromUser() {
        return scanner.nextLine();
    }

}
