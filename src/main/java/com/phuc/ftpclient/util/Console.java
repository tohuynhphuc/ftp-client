package com.phuc.ftpclient.util;

import com.phuc.ftpclient.Constants;

public class Console {

    public static void announce(String message) {
        System.out.println(Constants.CYAN + message + Constants.RESET);
    }

    public static void error(String message) {
        System.err.println(Constants.RED + message + Constants.RESET);
    }

    public static void warning(String message) {
        System.err.println(Constants.PURPLE + message + Constants.RESET);
    }

    public static void message(String message) {
        System.err.println(message);
    }

}
