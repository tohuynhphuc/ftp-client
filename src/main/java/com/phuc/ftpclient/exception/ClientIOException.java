package com.phuc.ftpclient.exception;

public class ClientIOException extends CustomException {

    public ClientIOException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String getType() {
        return "CLIENT ERROR";
    }

}
