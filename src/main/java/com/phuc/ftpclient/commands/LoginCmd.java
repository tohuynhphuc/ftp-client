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

public class LoginCmd extends BaseCmd {

    public LoginCmd() {
        purpose = Purpose.MESSAGE;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getUsage() {
        String usage = "Logs in to the system with username and password or anonymously.\n\tUsage: " + getName()
                + " <username> <password> OR " + getName();
        return usage;
    }

    @Override
    public boolean execute(ArrayList<String> args)
            throws InvalidArgumentsException, ClientIOException, ServerException, IOException {
        int argsCount = 2;

        System.out.println(args.size());

        if (args.size() != argsCount && !args.isEmpty()) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " or 0 arguments for command " + getName() + ".");
        }

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);

        if (args.isEmpty() || args.get(0).equals("")) {
            // TODO: Find out about anonymous login?
            App.getClient().sendMessage("USER anonymous");
            ServerResponse response = ReceiveMessageThread.receiveMessages();
            return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
        } else {
            App.getClient().sendMessage("USER " + args.get(0));
            ReceiveMessageThread.receiveMessages();
            App.getClient().sendMessage("PASS " + args.get(1));
            ServerResponse response = ReceiveMessageThread.receiveMessages();
            return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
        }
    }
}
