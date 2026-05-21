package com.phuc.ftpclient.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.phuc.ftpclient.App;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.threads.Purpose;
import com.phuc.ftpclient.util.ReceiveMessage;
import com.phuc.ftpclient.util.ServerResponse;

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
    public boolean execute(ArrayList<String> args)
            throws InvalidArgumentsException, ClientIOException, ServerException, IOException {
        int argsCount = 2;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        CommandHandler.getInstance().setPurpose(Purpose.DOWNLOAD);
        CommandHandler.getInstance().setPathToFile(args.get(1));
        App.getClient().sendMessage("PASV");
        ReceiveMessage.receiveMessages();
        App.getClient().sendMessage("RETR " + args.get(0));
        ReceiveMessage.receiveMessages();
        ServerResponse response = ReceiveMessage.receiveMessages();

        return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
    }

}
