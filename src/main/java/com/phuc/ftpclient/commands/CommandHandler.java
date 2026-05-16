package com.phuc.ftpclient.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.phuc.ftpclient.Client;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;

public class CommandHandler {

    private static final CommandHandler instance = new CommandHandler();

    private final Map<String, ICommand> map = new HashMap<>();

    private Purpose purpose = Purpose.MESSAGE;

    private CommandHandler() {
        registerCommand(new LoginCmd());
        registerCommand(new PrintWorkingDirCmd());
        registerCommand(new HelpCmd(this));
        registerCommand(new ListCmd());
        registerCommand(new GetCmd());
    }

    public static CommandHandler getInstance() {
        return instance;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void executeCommand(Client client, String userCommand) throws ClientIOException, InvalidArgumentsException {
        String commandSequence = processCommand(userCommand);

        String[] commandList = commandSequence.split("\n");
        for (String command : commandList) {
            client.sendMessage(command);
        }
    }

    private String processCommand(String userCommand) throws InvalidArgumentsException {

        String[] commandList = userCommand.split(" ");

        /*
         * For testing purposes. If I type an unimplemented command, input the
         * command directly onto the server.
         */
        if (!hasCommand(commandList[0])) {
            return userCommand;
        }
        ICommand command = getCommand(commandList[0]);
        ArrayList<String> args = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(commandList, 1, commandList.length)));

        String commandSequence = command.buildCommand(args);
        return commandSequence;
    }

    public ICommand[] getCommandArray() {
        return map.values().toArray(ICommand[]::new);
    }

    public boolean hasCommand(String name) {
        return map.containsKey(name);
    }

    public ICommand getCommand(String name) {
        return map.get(name);
    }

    private void registerCommand(ICommand command) {
        map.put(command.getName(), command);
    }

}
