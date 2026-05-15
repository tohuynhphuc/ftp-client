package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;

public class PrintWorkingDirCmd extends BaseCmd {

    @Override
    public String getName() {
        return "pwd";
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        if (!args.isEmpty()) {
            throw new InvalidArgumentsException("Error: Expecting 0 arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        // try {
        command.append("PWD").append("\n");
        // Console.message("[Server] " + client.receiveMessage());
        // } catch (IOException e) {
        // Console.error("[Client Error] " + e.getMessage());
        // } catch (ServerException e) {
        // Console.error("[Client Error] " + e.getMessage());
        // }
        return command.toString();
    }

}
