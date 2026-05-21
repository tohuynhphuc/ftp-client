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

public class PrintWorkingDirCmd extends BaseCmd {

    public PrintWorkingDirCmd() {
        purpose = Purpose.MESSAGE;
    }

    @Override
    public String getName() {
        return "pwd";
    }

    @Override
    public String getUsage() {
        String usage = "Prints working directory.\n\tUsage: " + getName();
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
        App.getClient().sendMessage("PWD");
        ServerResponse response = ReceiveMessageThread.receiveMessages();

        return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
    }

}
