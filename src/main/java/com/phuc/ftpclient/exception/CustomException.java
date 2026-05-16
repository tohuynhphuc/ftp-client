package com.phuc.ftpclient.exception;

import com.phuc.ftpclient.util.Console;

public abstract class CustomException extends Exception {

    public abstract String getType();

    public CustomException(String errorMessage) {
        super(errorMessage);
    }

    public void announceError() {
        Console.error("[" + getType() + "] " + getMessage());
        printStackTrace();
    }

}
