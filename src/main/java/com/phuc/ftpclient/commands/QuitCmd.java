package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;

public class QuitCmd extends BaseCmd {

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String getUsage() {
        String usage = "Quits the program.\n\tUsage: " + getName();
        return usage;
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        int argsCount = 0;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        command.append("QUIT").append("\n");
        return command.toString();
    }

}
