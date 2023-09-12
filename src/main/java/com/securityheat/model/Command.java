package com.securityheat.model;

public interface Command {

    String getCommand();
    String execute(String command, String[] args, String chatid, String ownerid);
}
