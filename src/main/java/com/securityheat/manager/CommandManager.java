package com.securityheat.manager;

import com.securityheat.Main;
import com.securityheat.model.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private List<Command> commands = new ArrayList<>();
    private Main instance;

    public CommandManager(Main instance){
        this.instance = instance;
    }

    public boolean isCommand(String message){
        return false;
    }

    public String run(String command){
        return "";
    }
}
