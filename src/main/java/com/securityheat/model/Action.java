package com.securityheat.model;

import org.json.JSONObject;

public interface Action {

    String getName();
    JSONObject getDefault();
    String execute(String title, String actionid, String chatid, String userid, String message, JSONObject json);
}
