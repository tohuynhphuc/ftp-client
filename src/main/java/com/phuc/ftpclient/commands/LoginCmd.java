package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;

public class LoginCmd extends BaseCmd {

    public LoginCmd() {

    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        if (args.size() != 2) {
            throw new InvalidArgumentsException("Error: Expecting 2 arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        command.append("USER ").append(args.get(0)).append("\n");
        command.append("PASS ").append(args.get(1)).append("\n");
        return command.toString();
    }

    @Override
    public String getUsage() {
        String usage = "Logs in to the system with username and password.\n\tUsage: " + getName() + " <username> <password>";
        return usage;
    }

}
