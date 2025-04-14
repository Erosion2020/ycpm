package org.erosion2020.util;

import java.util.Arrays;
import java.util.List;

public class CommandCursor {
    private final List<String> tokens;
    private final String splitStr;
    private int cursor = 0;

    private String current;

    public CommandCursor(String command) {
        this.splitStr = ":";
        this.tokens = Arrays.asList(command.split(splitStr));
    }

    public CommandCursor(String command, String splitChar) {
        this.splitStr = splitChar;
        this.tokens = Arrays.asList(command.split(splitChar));
    }

    public String next() {
        if (cursor >= tokens.size()) {
            return null;
        }
        current = tokens.get(cursor++);
        return current;
    }

    public String current() {
        return current;
    }

    public void reset() {
        cursor = 0;
        current = null;
    }

    public boolean parse(String command){
        CommandCursor commandCursor = new CommandCursor(command, ":");
        String token = commandCursor.next();
        switch (token) {
            case "mem":
                String mem_type = commandCursor.next();
                switch (mem_type) {
                    case "bx":
                    case "yj":
                    default:
                        return false;
                }
            case "mem-loader":
                String loader_type = commandCursor.next();
            default:
                return true;
        }
    }
}
