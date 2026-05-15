package com.phuc.ftpclient.commands;

import java.io.IOException;
import java.util.Map;

import com.phuc.ftpclient.Client;

public class CommandHandler {

    private Map<String, ICommand> map;

    public CommandHandler() {
        registerCommand(new LoginCmd());
        registerCommand(new PrintWorkingDirCmd());
    }

    public void executeCommand(Client client, String commandss) throws IOException {
        String[] commandList = commandss.split("\n");
        for (String command : commandList) {
            client.sendMessage(command);
        }
    }

    public ICommand getCommand(String name) {
        return map.get(name);
    }

    private void registerCommand(ICommand command) {
        map.put(command.getName(), command);
    }

}
