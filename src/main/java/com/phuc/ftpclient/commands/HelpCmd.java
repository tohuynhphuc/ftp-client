package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.threads.Purpose;
import com.phuc.ftpclient.util.Console;

public class HelpCmd extends BaseCmd {

    private final CommandHandler handler;

    public HelpCmd(CommandHandler handler) {
        this.handler = handler;
        purpose = Purpose.MESSAGE;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        String usage = "Shows all commands.\n\tUsage: " + getName();
        return usage;
    }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        ICommand[] commandArray = handler.getCommandArray();

        for (ICommand command : commandArray) {
            Console.message(command.getName() + "\t" + command.getUsage());
        }

        return "\n";
    }

}
