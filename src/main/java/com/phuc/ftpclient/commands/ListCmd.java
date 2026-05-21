package com.phuc.ftpclient.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.phuc.ftpclient.App;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.threads.Purpose;
import com.phuc.ftpclient.threads.ReceiveMessageThread;
import com.phuc.ftpclient.util.ServerResponse;

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
    public boolean execute(ArrayList<String> args)
            throws InvalidArgumentsException, ClientIOException, ServerException, IOException {
        int argsCount = 0;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);
        App.getClient().sendMessage("PASV");
        ReceiveMessageThread.receiveMessages();
        App.getClient().sendMessage("LIST");
        ReceiveMessageThread.receiveMessages();
        ServerResponse response = ReceiveMessageThread.receiveMessages();

        return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
    }

}
