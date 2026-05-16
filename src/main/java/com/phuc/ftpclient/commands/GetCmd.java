package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class GetCmd extends BaseCmd {

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getUsage() {
        String usage = "Downloads file from server to the specified location with the specified name.\n\tUsage: "
                + getName() + " <file> <location> <fileName>";
        return usage;
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        if (args.size() != 3) {
            throw new InvalidArgumentsException("Error: Expecting 3 arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        command.append("PASV").append("\n");
        // TODO: Currently hard coding
        command.append("RETR ").append("test.txt").append("\n");

        CommandHandler.getInstance().setPurpose(Purpose.DOWNLOAD);

        return command.toString();
    }

}
