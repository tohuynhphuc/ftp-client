package com.phuc.ftpclient.exception;

import com.phuc.ftpclient.util.Console;

public class ServerException extends CustomException {

    public ServerException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String getType() {
        return "SERVER ERROR";
    }

    @Override
    public void announceError() {
        Console.error("[" + getType() + "] " + getMessage());
    }

}
