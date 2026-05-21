package com.phuc.ftpclient.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.threads.PassiveSocketThread;
import com.phuc.ftpclient.threads.Purpose;

public class CommandHandler {

    private static final CommandHandler instance = new CommandHandler();

    private final Map<String, ICommand> map = new HashMap<>();

    private Purpose purpose = Purpose.MESSAGE;
    private String pathToFile;

    private PassiveSocketThread pasvSocketThread;

    private CommandHandler() {
        registerCommand(new HelpCmd(this));
        registerCommand(new LoginCmd());
        registerCommand(new PrintWorkingDirCmd());
        registerCommand(new ChangeDirCmd());
        registerCommand(new ListCmd());
        registerCommand(new GetCmd());
        registerCommand(new PutCmd());
        registerCommand(new DeleteCmd());
        registerCommand(new MakeDirCmd());
        registerCommand(new RemoveDirCmd());
        registerCommand(new QuitCmd());
        registerCommand(new MLSDCmd());
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

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void executeCommand(String userCommand)
            throws ClientIOException, InvalidArgumentsException, ServerException, IOException {
        String[] commandList = userCommand.split(" ");

        ICommand command = getCommand(commandList[0]);
        ArrayList<String> args = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(commandList, 1, commandList.length)));

        command.execute(args);
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

    public PassiveSocketThread getPasvSocketThread() {
        return pasvSocketThread;
    }

    public void setPasvSocketThread(PassiveSocketThread pasvSocketThread) {
        this.pasvSocketThread = pasvSocketThread;
    }

}
