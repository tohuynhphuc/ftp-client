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
import com.phuc.ftpclient.util.ReceiveMessage;
import com.phuc.ftpclient.util.ServerResponse;
import com.phuc.ftpclient.util.SocketAddress;

public class PassiveSocketThread extends Thread {

    private final Purpose purpose;
    private final SocketAddress addr;
    private String pathToFile;
    private boolean isMlsdReady = false;
    private String mlsdResponse;

    public PassiveSocketThread(ServerResponse response, Purpose purpose) {
        this.purpose = purpose;
        this.addr = new SocketAddress(response.getMessage());
    }

    public PassiveSocketThread(ServerResponse response, Purpose purpose, String pathToFile) {
        this.purpose = purpose;
        this.addr = new SocketAddress(response.getMessage());
        this.pathToFile = pathToFile;
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
                case Purpose.MLSD -> {
                    BufferedReader dataReader = new BufferedReader(
                            new InputStreamReader(dataSocket.getInputStream()));

                    String line;
                    mlsdResponse = "";
                    while (!Thread.currentThread().isInterrupted() && (line = dataReader.readLine()) != null) {
                        if (line.startsWith("type=")) {
                            Console.message("FOUND LINE: " + line);
                            mlsdResponse += line + "\n";
                        }
                    }
                    synchronized (this) {
                        isMlsdReady = true;
                        notifyAll();
                    }
                }
                case Purpose.DOWNLOAD -> {
                    Console.debug("File Download starting...");
                    InputStream dataInputStream = dataSocket.getInputStream();
                    // FileOutputStream fileOutputStream = new FileOutputStream(Constants.LOCAL_DIR
                    // + pathToFile);
                    FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);
                    dataInputStream.transferTo(fileOutputStream);
                    Console.debug("File Download complete.");
                }
                case Purpose.UPLOAD -> {
                    Console.debug("File Upload starting...");
                    OutputStream dataOutputStream = dataSocket.getOutputStream();
                    // try (FileInputStream fileInputStream = new
                    // FileInputStream(Constants.LOCAL_DIR + pathToFile)) {
                    try (FileInputStream fileInputStream = new FileInputStream(pathToFile)) {
                        fileInputStream.transferTo(dataOutputStream);
                    }
                    Console.debug("File Upload complete.");
                }

                default -> throw new AssertionError();
            }
        } catch (IOException ex) {
            System.getLogger(ReceiveMessage.class.getName()).log(System.Logger.Level.ERROR,
                    (String) null, ex);
        }

        Console.announce("Passive socket thread ended.");
    }

    public boolean getIsMlsdReady() {
        return isMlsdReady;
    }

    public synchronized String getMlsdResponse() throws InterruptedException {
        while (!isMlsdReady) {
            wait();
        }
        isMlsdReady = false;
        return mlsdResponse;
    }

}
