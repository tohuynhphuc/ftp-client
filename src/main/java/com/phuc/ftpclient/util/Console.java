package com.phuc.ftpclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Console {

    private static TextArea responseTA = null;

    private static final List<Consumer<String>> listenersList = new ArrayList<>();

    public static void addListener(Consumer<String> listener) {
        listenersList.add(listener);
    }

    public static void removeListener(Consumer<String> listener) {
        listenersList.add(listener);
    }

    public static void announce(String message) {
        printWithColor(message, Constants.CYAN);
    }

    public static void error(String message) {
        printWithColor(message, Constants.RED);
    }

    public static void warning(String message) {
        printWithColor(message, Constants.PURPLE);
    }

    public static void message(String message) {
        printWithColor(message, Constants.RESET);
    }

    public static void debug(String message) {
        printWithColor(message, Constants.GREEN);
    }

    private static void printWithColor(String message, String color) {
        for (Consumer<String> listener : listenersList) {
            listener.accept(message);
        }

        if (Constants.IS_CONSOLE) {
            message = color + message + Constants.RESET;
            System.out.println(message);
        } else if (responseTA != null) {
            String finalMessage = message;
            Platform.runLater(() -> {
                responseTA.appendText(finalMessage + "\n");
            });
        }
    }

    public static TextArea getResponseTA() {
        return responseTA;
    }

    public static void setResponseTA(TextArea responseTA) {
        Console.responseTA = responseTA;
    }

}
