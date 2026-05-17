package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class RemoveDirCmd extends BaseCmd {

    @Override
    public String getName() {
        return "rmdir";
    }

    @Override
    public String getUsage() {
        String usage = "Removes directory.\n\tUsage: "
                + getName() + " <directory>";
        return usage;
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        int argsCount = 1;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        command.append("RMD ").append(args.get(0)).append("\n");

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);

        return command.toString();
    }

}
