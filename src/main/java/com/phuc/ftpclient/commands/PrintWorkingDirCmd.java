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
        command.append("PWD").append("\n");
        return command.toString();
    }

    @Override
    public String getUsage() {
        String usage = "Prints working directory.\n\tUsage: " + getName();
        return usage;
    }

}
