package com.phuc.ftpclient.threads;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.phuc.ftpclient.util.Console;
import com.phuc.ftpclient.util.Constants;
import com.phuc.ftpclient.util.ServerResponse;
import com.phuc.ftpclient.util.SocketAddress;

public class PassiveSocketThread extends Thread {

    private final Purpose purpose;
    private final SocketAddress addr;

    public PassiveSocketThread(ServerResponse response, Purpose purpose) {
        this.purpose = purpose;
        this.addr = new SocketAddress(response.getMessage());
    }

    @Override
    public void run() {
        try (Socket dataSocket = new Socket(addr.getAddress(), addr.getPort())) {
            switch (purpose) {
                case Purpose.MESSAGE -> {
                    BufferedReader dataReader = new BufferedReader(
                            new InputStreamReader(dataSocket.getInputStream()));

                    String line;
                    while (!Thread.currentThread().isInterrupted() && (line = dataReader.readLine()) != null) {
                        Console.message(line);
                    }
                }
                case Purpose.DOWNLOAD -> {
                    Console.debug("File Download starting...");
                    InputStream dataInputStream = dataSocket.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(Constants.LOCAL_DIR + "/test_123/");
                    dataInputStream.transferTo(fileOutputStream);
                    Console.debug("File Download complete.");
                }
                case Purpose.UPLOAD -> {
                    Console.debug("File Upload starting...");
                    OutputStream dataOutputStream = dataSocket.getOutputStream();
                    try (FileInputStream fileInputStream = new FileInputStream(
                            Constants.LOCAL_DIR + "/upload/to-be-upload.txt")) {
                        fileInputStream.transferTo(dataOutputStream);
                    }
                    Console.debug("File Upload complete.");
                }

                default -> throw new AssertionError();
            }
        } catch (IOException ex) {
            System.getLogger(ReceiveMessageThread.class.getName()).log(System.Logger.Level.ERROR,
                    (String) null, ex);
        }

        Console.announce("Passive socket thread ended.");
    }

}
