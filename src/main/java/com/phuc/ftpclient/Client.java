package com.phuc.ftpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public void connect() {
        try {
            Socket socket = new Socket("ftp.gnu.org", 21);

            BufferedReader networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String receive = "receiving";
            while (receive != null) {
                System.out.println("[Server] " + receive);
                receive = networkIn.readLine();
            }
            System.out.println("Not receive any more messages");

            socket.close();
        } catch (IOException ex) {
            System.err.println("[Client Error] " + ex.getMessage());
        }
    }

}
