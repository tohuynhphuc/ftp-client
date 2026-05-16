package com.phuc.ftpclient.exception;

public class InvalidArgumentsException extends CustomException {

    public InvalidArgumentsException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String getType() {
        return "COMMAND ERROR";
    }

}
