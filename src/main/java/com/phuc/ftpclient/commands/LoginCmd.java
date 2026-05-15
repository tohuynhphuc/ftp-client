package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;

public class LoginCmd extends BaseCmd {

    private String username;
    private String password;

    public LoginCmd() {
        // this.username = username;
        // this.password = password;
    }

    @Override
    public String getName() {
        return "login";
    }

    // @Override
    // public void execute(Client client) {
    // try {
    // client.sendMessage("USER " + username);
    // client.receiveMessage();
    // client.sendMessage("PASS " + password);
    // client.receiveMessage();
    // } catch (IOException e) {
    // Console.error("[Client Error] " + e.getMessage());
    // } catch (ServerException e) {
    // Console.warning("[Server Error] " + e.getMessage());
    // }
    // }

    @Override
    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException {
        if (args.size() != 2) {
            throw new InvalidArgumentsException("Error: Expecting 2 arguments for command " + getName() + ".");
        }

        StringBuilder command = new StringBuilder();
        command.append("USER ").append(username).append("\n");
        command.append("PASS ").append(password).append("\n");
        return command.toString();
    }

}
