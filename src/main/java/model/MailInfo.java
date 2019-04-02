package model;

import java.util.ArrayList;

public class MailInfo {

    private final ArrayList<String> mailInfoList;
    private ArrayList<String> requiredCommands;
    private final ArrayList<String> commandsMailInfoList;
    private final String to = "To";
    private final String from = "From";
    private final String subject = "Subject";
    private final String contentType = "Content-Type";
    private final String empty = "";
    private final String point = ".";

    public MailInfo() {
        this.mailInfoList = new ArrayList<>();
        commandsMailInfoList = new ArrayList<>();
        createRequiredCommands();
    }

    private void createRequiredCommands() {
        this.requiredCommands = new ArrayList<>();
        requiredCommands.add(to);
        requiredCommands.add(from);
        requiredCommands.add(subject);
        requiredCommands.add(contentType);
        requiredCommands.add(empty);
        requiredCommands.add(point);
    }

    public boolean checkSyntax() {

        return true;
    }

    public boolean isContainsMinCommands() {
        if (commandsMailInfoList.isEmpty()) {
            return false;
        }
        return commandsMailInfoList.containsAll(requiredCommands);
    }

    private String getFirstWord(String str) {
        try {
            return str.split(":", 2)[0];
        } catch (RuntimeException e) {
            return null;
        }
    }

    public ArrayList<String> getMailInfoList() {
        return this.mailInfoList;
    }

    public void add(String info) {
        this.mailInfoList.add(info);
        this.commandsMailInfoList.add(getFirstWord(info));
    }

    public void clearInfo() {
        this.mailInfoList.clear();
        this.commandsMailInfoList.clear();
    }
}