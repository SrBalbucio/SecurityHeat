package com.securityheat.manager;

import com.securityheat.Main;
import com.securityheat.model.Action;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {

    private List<Action> cachedActions = new ArrayList<>();
    private Main instance;

    public ActionManager(Main instance){
        this.instance = instance;
    }

    public void add(String name, Action action, JSONObject json){

    }
}
