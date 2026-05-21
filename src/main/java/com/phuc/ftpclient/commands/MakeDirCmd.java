package com.phuc.ftpclient.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.phuc.ftpclient.App;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.state.State;
import com.phuc.ftpclient.state.StateMachine;
import com.phuc.ftpclient.threads.Purpose;
import com.phuc.ftpclient.util.ReceiveMessage;
import com.phuc.ftpclient.util.ServerResponse;

public class MakeDirCmd extends BaseCmd {

    @Override
    public String getName() {
        return "mkdir";
    }

    @Override
    public String getUsage() {
        String usage = "Creates a new directory at the current location.\n\tUsage: "
                + getName() + " <directory_name>";
        return usage;
    }

    @Override
    public boolean execute(ArrayList<String> args)
            throws InvalidArgumentsException, ClientIOException, ServerException, IOException {
        int argsCount = 1;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);
        App.getClient().sendMessage("MKD " + args.get(0));
        ServerResponse response = ReceiveMessage.receiveMessages();

        StateMachine.getInstance().switchState(State.COMD);

        return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
    }

}
