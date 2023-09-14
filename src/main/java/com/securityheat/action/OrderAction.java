package com.securityheat.action;

import com.securityheat.model.Action;
import org.json.JSONObject;

public class OrderAction implements Action {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public JSONObject getDefault() {
        return null;
    }

    @Override
    public String execute(String title, String actionid, String chatid, String userid, String message, JSONObject json) {
        return null;
    }
}
