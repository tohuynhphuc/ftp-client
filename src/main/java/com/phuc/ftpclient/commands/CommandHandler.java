package com.phuc.ftpclient.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.phuc.ftpclient.Client;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;

public class CommandHandler {

    private final Map<String, ICommand> map = new HashMap<>();

    public CommandHandler() {
        registerCommand(new LoginCmd());
        registerCommand(new PrintWorkingDirCmd());
        registerCommand(new HelpCmd(this));
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

        ICommand command = getCommand(commandList[0]);
        ArrayList<String> args = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(commandList, 1, commandList.length)));

        String commandSequence = command.buildCommand(args);
        return commandSequence;
    }

    public ICommand[] getCommandArray() {
        return map.values().toArray(new ICommand[0]);
    }

    public ICommand getCommand(String name) {
        return map.get(name);
    }

    private void registerCommand(ICommand command) {
        map.put(command.getName(), command);
    }

}
