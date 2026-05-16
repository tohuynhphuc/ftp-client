package com.phuc.ftpclient.exception;

public class ServerException extends CustomException {

    public ServerException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String getType() {
        return "SERVER ERROR";
    }

}
