package com.securityheat.manager;

import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import com.securityheat.Main;
import com.securityheat.model.Action;
import org.json.JSONObject;

import java.util.*;

public class ActionManager {

    private List<Action> actions = new ArrayList<>();
    private Main instance;
    private HikariSQLiteInstance sqlite;

    public ActionManager(Main instance){
        this.instance = instance;
        this.sqlite = instance.getSqlite();
        sqlite.createTable("actions", "uid VARCHAR(255), title VARCHAR(255), data TEXT");
    }

    public void createAction(String chatid, String action){
        Action a = actions.stream().filter(ac -> ac.getName().equalsIgnoreCase(action)).findFirst().get();
        UUID uid = UUID.randomUUID();
        sqlite.insert("uid, title, data", "'"+uid.toString()+"', '"+a.getName()+"', '"+a.getDefault().toString()+"'", "actions");
        instance.getChatManager().setAction(action, chatid);
    }

    public String execute(String actionid, String userid, String chatid, String message){
        JSONObject json = new JSONObject(sqlite.get("uid", "=", chatid, "data", "actions"));
        String title = json.getString("title");
        return actions.stream().filter(a -> a.getName().equalsIgnoreCase(title))
                .findFirst().get().execute(title, actionid, chatid, userid, message, json);
    }
}
