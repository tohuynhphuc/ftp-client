package com.phuc.ftpclient.util;

public class SocketAddress {

    private final String address;
    private final int port;

    public SocketAddress(String announcement) {
        String[] connection = announcement.split("\\(")[1].split("\\)")[0].split(",");
        address = connection[0] + "." + connection[1] + "." +
                connection[2] + "." + connection[3];
        port = Integer.parseInt(connection[4]) * 256 + Integer.parseInt(connection[5]);
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

}
