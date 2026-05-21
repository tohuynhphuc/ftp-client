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
    public boolean execute(ArrayList<String> args)
            throws InvalidArgumentsException, ClientIOException, ServerException, IOException {
        int argsCount = 0;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        CommandHandler.getInstance().setPurpose(Purpose.MLSD);
        App.getClient().sendMessage("PASV");
        ReceiveMessageThread.receiveMessages();
        App.getClient().sendMessage("MLSD");
        ReceiveMessageThread.receiveMessages();
        ServerResponse response = ReceiveMessageThread.receiveMessages();

        return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
    }

}
