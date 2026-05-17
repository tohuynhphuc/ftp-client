package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class DeleteCmd extends BaseCmd {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getUsage() {
        String usage = "Deletes a file.\n\tUsage: "
                + getName() + " <path_to_file>";
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
        command.append("DELE ").append(args.get(0)).append("\n");

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);

        return command.toString();
    }
    
}
