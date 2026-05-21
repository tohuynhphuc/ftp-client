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
import com.phuc.ftpclient.threads.ReceiveMessageThread;
import com.phuc.ftpclient.util.ServerResponse;

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
    public boolean execute(ArrayList<String> args)
            throws InvalidArgumentsException, ClientIOException, ServerException, IOException {
        int argsCount = 2;

        if (args.size() != argsCount) {
            throw new InvalidArgumentsException(
                    "Error: Expecting " + argsCount + " arguments for command " + getName() + ".");
        }

        CommandHandler.getInstance().setPurpose(Purpose.UPLOAD);
        CommandHandler.getInstance().setPathToFile(args.get(0));
        App.getClient().sendMessage("PASV");
        ReceiveMessageThread.receiveMessages();
        App.getClient().sendMessage("STOR " + args.get(1));
        ReceiveMessageThread.receiveMessages();
        ServerResponse response = ReceiveMessageThread.receiveMessages();

        StateMachine.getInstance().switchState(State.COMD);

        return response.getMessageCode() >= 200 && response.getMessageCode() <= 399;
    }

}
