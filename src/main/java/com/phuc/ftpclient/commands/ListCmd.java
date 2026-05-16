package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class ListCmd extends BaseCmd {

    public ListCmd() {
        purpose = Purpose.MESSAGE;
    }

    @Override
    public String getName() {
        return "ls";
    }

    @Override
    public String getUsage() {
        String usage = "Lists files in working directory.\n\tUsage: " + getName();
        return usage;
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        if (!args.isEmpty()) {
            throw new InvalidArgumentsException("Error: Expecting 0 arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        command.append("PASV").append("\n");
        command.append("LIST").append("\n");

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);

        return command.toString();
    }

}
