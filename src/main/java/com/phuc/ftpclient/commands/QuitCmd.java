package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.FTPApplication;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.state.State;
import com.phuc.ftpclient.state.StateMachine;
import com.phuc.ftpclient.threads.Purpose;
import com.phuc.ftpclient.util.ReceiveMessage;

public class QuitCmd extends BaseCmd {

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String getUsage() {
        String usage = "Quits the program.\n\tUsage: " + getName();
        return usage;
    }

    @Override
    public boolean execute(ArrayList<String> args)
            throws InvalidArgumentsException, ClientIOException, ServerException {
        int argsCount = 0;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        CommandHandler.getInstance().setPurpose(Purpose.MESSAGE);
        FTPApplication.getClient().sendMessage("QUIT");
        ReceiveMessage.receiveMessages();
        FTPApplication.getClient().closeConnection();
        StateMachine.getInstance().switchState(State.SHUT);
        return true;
    }

}
