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

public class PassiveSocketThread extends Thread {

    private final String message;
    private final Purpose purpose;

    public PassiveSocketThread(String message, Purpose purpose) {
        this.message = message;
        this.purpose = purpose;
    }

    @Override
    public void run() {
        try {
            String[] connection = message.split("\\(")[1].split("\\)")[0].split(",");
            String address = connection[0] + "." + connection[1] + "." +
                    connection[2] + "." + connection[3];
            int port = Integer.parseInt(connection[4]) * 256 + Integer.parseInt(connection[5]);

            try (Socket dataSocket = new Socket(address, port)) {
                // Thread.sleep(500);
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
                        Console.announce("File Download starting...");
                        InputStream dataInputStream = dataSocket.getInputStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(Constants.LOCAL_DIR + "/test_123/");
                        dataInputStream.transferTo(fileOutputStream);
                        Console.announce("File Download complete.");
                    }
                    case Purpose.UPLOAD -> {
                        Console.announce("File Upload starting...");
                        OutputStream dataOutputStream = dataSocket.getOutputStream();
                        try (FileInputStream fileInputStream = new FileInputStream(
                                Constants.LOCAL_DIR + "/upload/to-be-upload.txt")) {
                            fileInputStream.transferTo(dataOutputStream);
                        }
                        Console.announce("File Upload complete.");
                    }

                    default -> throw new AssertionError();
                }
            }
        } catch (IOException ex) {
            System.getLogger(ReceiveMessageThread.class.getName()).log(System.Logger.Level.ERROR,
                    (String) null, ex);
        }

        Console.announce("Passive socket thread ended.");
    }

}
