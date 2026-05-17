package com.phuc.ftpclient.util;

public class ServerResponse {

    private final int messageCode;
    private final String message;

    public ServerResponse(String fullMessage) {
        messageCode = Integer.parseInt(fullMessage.substring(0, 3));
        message = fullMessage.substring(4);
    }

    public int getMessageCode() {
        return messageCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Code: ").append(messageCode);
        sb.append("\nMessage: ").append(message);
        return sb.toString();
    }

}
