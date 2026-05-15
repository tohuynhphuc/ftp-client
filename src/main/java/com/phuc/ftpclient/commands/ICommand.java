package com.phuc.ftpclient.commands;

import java.util.ArrayList;

import com.phuc.ftpclient.exception.InvalidArgumentsException;

public interface ICommand {

    public String getName();

    public String buildCommand(ArrayList<String> args) throws InvalidArgumentsException;

}
