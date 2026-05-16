package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class PrintWorkingDirCmd extends BaseCmd {

    public PrintWorkingDirCmd() {
        purpose = Purpose.MESSAGE;
    }

    @Override
    public String getName() {
        return "pwd";
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        int argsCount = 0;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
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
