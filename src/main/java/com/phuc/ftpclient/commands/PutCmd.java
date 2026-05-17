package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class PutCmd extends BaseCmd {

    @Override
    public String getName() {
        return "put";
    }

    @Override
    public String getUsage() {
        String usage = "Uploads file to server to the specified location with the specified name. Will not create a new folder if path does not exist.\n\tUsage: "
                + getName() + " <path_to_local_file> <path_to_server_file>";
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
        // TODO: Currently hard coding
        command.append("STOR ").append(args.get(1)).append("\n");

        CommandHandler.getInstance().setPurpose(Purpose.UPLOAD);
        CommandHandler.getInstance().setPathToFile(args.get(0));

        return command.toString();
    }

}
