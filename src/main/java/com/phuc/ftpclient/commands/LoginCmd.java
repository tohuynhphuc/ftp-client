package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class LoginCmd extends BaseCmd {

    public LoginCmd() {
        purpose = Purpose.MESSAGE;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getUsage() {
        String usage = "Logs in to the system with username and password.\n\tUsage: " + getName()
                + " <username> <password>";
        return usage;
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        int argsCount = 2;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        command.append("USER ").append(args.get(0)).append("\n");
        command.append("PASS ").append(args.get(1)).append("\n");
        return command.toString();
    }

}
