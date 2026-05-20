package com.phuc.ftpclient.util;

public class MLSDEntry {

    private final String type;
    private final String name;

    public MLSDEntry(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public MLSDEntry(String line) {
        String[] parts = line.split(" ", 2);

        name = parts[1];

        String[] facts = parts[0].split(";");

        for (String fact : facts) {
            if (fact.startsWith("type=")) {
                type = fact.substring(5);
                return;
            }
        }
        type = "";
    }

    public boolean isDirectory() {
        return type.equalsIgnoreCase("dir");
    }

    public String getName() {
        return name;
    }

}
