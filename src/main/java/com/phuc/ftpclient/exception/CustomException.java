package com.phuc.ftpclient.exception;

import com.phuc.ftpclient.util.Console;

public abstract class CustomException extends Exception {

    public abstract String getType();

    public CustomException(String errorMessage) {
        super(errorMessage);
    }

    public void announceError() {
        // StackTraceElement location = getStackTrace()[0];
        Console.error("[" + getType() + "] " + getMessage());
        printStackTrace();
        // Console.error(" -> at " + location.getClassName() + "." +
        // location.getMethodName() + "()" + " line "
        // + location.getLineNumber());
    }

}
