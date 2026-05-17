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
                + getName() + " <path_to_server_file> <local_file_name>";
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
        command.append("PASV").append("\n");
        command.append("RETR ").append(args.get(0)).append("\n");

        CommandHandler.getInstance().setPurpose(Purpose.DOWNLOAD);
        CommandHandler.getInstance().setPathToFile(args.get(1));

        return command.toString();
    }

}
