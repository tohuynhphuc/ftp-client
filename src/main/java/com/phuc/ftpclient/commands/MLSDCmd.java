package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class MLSDCmd extends BaseCmd {

    public MLSDCmd() {
        purpose = Purpose.MESSAGE;
    }

    @Override
    public String getName() {
        return "mlsd";
    }

    @Override
    public String getUsage() {
        String usage = "MLSD - Lists files in working directory.\n\tUsage: " + getName();
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
        command.append("PASV").append("\n");
        command.append("MLSD").append("\n");

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);

        return command.toString();
    }

}
