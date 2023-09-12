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
        return message.startsWith("!") && commands.stream().anyMatch(c -> c.getCommand().equalsIgnoreCase(message.split(" ")[0]));
    }

    public String run(String command, String owner, String chat){
        return commands.stream().filter(c -> c.getCommand().equalsIgnoreCase(command.split(" ")[0]))
                .findFirst().get().execute(command, command.split(" "), owner, chat);
    }
}
