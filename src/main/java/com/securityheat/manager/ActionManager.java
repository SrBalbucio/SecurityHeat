package com.securityheat.manager;

import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import com.securityheat.Main;
import com.securityheat.model.Action;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {

    private List<Action> actions = new ArrayList<>();
    private Main instance;
    private HikariSQLiteInstance sqlite;

    public ActionManager(Main instance){
        this.instance = instance;
        this.sqlite = instance.getSqlite();
        sqlite.createTable("actions", "uid VARCHAR(255), title VARCHAR(255), data TEXT");
    }

    public String execute(String actionid, String chatid, String message){
        JSONObject json = new JSONObject(sqlite.get("uid", "=", chatid, "data", "actions"));
        actions.stream().filter(a -> a.getName().equalsIgnoreCase(json.getString("title")));
    }
}
