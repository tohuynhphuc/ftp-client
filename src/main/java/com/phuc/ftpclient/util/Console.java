package com.phuc.ftpclient.util;

import javafx.scene.control.TextArea;

public class Console {

    private static TextArea responseTA = null;

    public static void announce(String message) {
        if (Constants.IS_CONSOLE) {
            message = Constants.CYAN + message + Constants.RESET;
            System.out.println(message);
        } else if (responseTA != null) {
            responseTA.setText(responseTA.getText() + message + "\n");
        }
    }

    public static void error(String message) {
        if (Constants.IS_CONSOLE) {
            message = Constants.RED + message + Constants.RESET;
            System.err.println(message);
        } else if (responseTA != null) {
            responseTA.setText(responseTA.getText() + message + "\n");
        }
    }

    public static void warning(String message) {
        if (Constants.IS_CONSOLE) {
            message = Constants.PURPLE + message + Constants.RESET;
            System.err.println(message);
        } else if (responseTA != null) {
            responseTA.setText(responseTA.getText() + message + "\n");
        }
    }

    public static void message(String message) {
        if (Constants.IS_CONSOLE) {
            System.err.println(message);
        } else if (responseTA != null) {
            responseTA.setText(responseTA.getText() + message + "\n");
        }
    }

    public static void debug(String message) {
        if (Constants.IS_CONSOLE) {
            message = Constants.GREEN + message + Constants.RESET;
            System.err.println(message);
        } else if (responseTA != null) {
            responseTA.setText(responseTA.getText() + message + "\n");
        }
    }

    public static TextArea getResponseTA() {
        return responseTA;
    }

    public static void setResponseTA(TextArea responseTA) {
        Console.responseTA = responseTA;
    }

}
