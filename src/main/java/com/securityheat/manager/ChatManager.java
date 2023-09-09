package com.securityheat.manager;

import balbucio.sqlapi.model.*;
import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import com.securityheat.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatManager {

    private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM");
    private static SimpleDateFormat SDF_MSG = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private Main instance;
    private HikariSQLiteInstance sqlite;

    public ChatManager(Main instance) {
        this.instance = instance;
        this.sqlite = instance.getSqlite();
        sqlite.createTable("chat", "uid VARCHAR(255), title VARCHAR(255), owner VARCHAR(255), creationdate BIGINT, state VARCHAR(255), img VARCHAR(255)");
        sqlite.createTable("messages", "uid VARCHAR(255), read BOOLEAN, message TEXT, owner VARCHAR(255), chat VARCHAR(255), time BIGINT");
    }

    public String createChat(String title, String owner, String img) {
        UUID uid = UUID.randomUUID();
        sqlite.insert("uid, title, owner, creationdate, state, img",
                "'" + uid.toString() + "', '" + title + "', '" + owner + "', '" + Calendar.getInstance().getTimeInMillis() + "', 'OPEN', '"+img+"'",
                "chat");
        return uid.toString();
    }

    public JSONArray getChats(String owner) {
        if (owner.equalsIgnoreCase("admin")) {
            return getAllChats();
        }
        List<Object> uids = sqlite.getAll("owner", "=", owner, "uid", "chat");
        JSONArray array = new JSONArray();
        uids.forEach(o -> {
            JSONObject chat = new JSONObject();
            long date = (long) sqlite.get("uid", "=", (String) o, "creationdate", "chat");
            chat.put("uid", o);
            chat.put("adm", false);
            chat.put("title", sqlite.get("uid", "=", (String) o, "title", "chat"));
            chat.put("desc", "Criado em " + SDF.format(new Date(date)));
            chat.put("state", sqlite.get("uid", "=", (String) o, "state", "chat"));
            chat.put("img", sqlite.get("uid", "=", (String) o, "img", "chat"));
            chat.put("creationdate", date);
            chat.put("owner", owner);
            chat.put("username", instance.getUserManager().getUsername(owner));
            array.put(chat);
        });
        return array;
    }

    public JSONArray getAllChats() {
        List<ResultValue> chats = sqlite.getAllValuesOrderedBy("creationdate", "chat");
        JSONArray array = new JSONArray();
        chats.forEach(o -> {
            if (o.asString("state").equalsIgnoreCase("OPEN")) {
                String title = o.asString("title");
                if(!title.equalsIgnoreCase("Central da Comunidade")) {
                    JSONObject chat = new JSONObject();
                    chat.put("uid", o.asString("uid"));
                    chat.put("adm", true);
                    chat.put("title", title);
                    chat.put("desc", "Clique para responder! - Criado em " + SDF.format(new Date(o.asLong("creationdate"))));
                    chat.put("state", o.asString("state"));
                    chat.put("img", o.asString("img"));
                    chat.put("creationdate", o.asLong("creationdate"));
                    chat.put("owner", o.asString("owner"));
                    chat.put("username", instance.getUserManager().getUsername(o.asString("owner")));
                    array.put(chat);
                }
            }
        });
        return array;
    }

    public String getFirstChat(String owner) {
        try {
            return sqlite
                    .getPreparedStatement("SELECT uid FROM chat WHERE owner = '" + owner + "' ORDER BY creationdate;")
                    .executeQuery().getString("uid");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray getMessages(String chat, String user) {
        List<Object> uids = sqlite.getAll("chat", "=", chat, "uid", "messages");
        JSONArray array = new JSONArray();
        uids.forEach(o -> {
            boolean read = Boolean.parseBoolean((String) sqlite.get("uid", "=", (String) o, "read", "messages"));
                String owner = (String) sqlite.get("uid", "=", (String) o, "owner", "messages");
                JSONObject message = new JSONObject();
                message.put("uid", o);
                message.put("message", sqlite.get("uid", "=", (String) o, "message", "messages"));
                message.put("owner", owner);
                message.put("time", SDF_MSG.format(new Date((long) sqlite.get("uid", "=", (String) o, "time", "messages"))) + " | " + (read ? "Visto" : "Entregue"));
                message.put("chat", chat);
                if (!owner.equalsIgnoreCase(user)) {
                    setRead((String) o, chat);
                }
                array.put(message);
        });
        return array;
    }

    public JSONArray getUnreadMessages(String chat, String user) {
        List<Object> uids = sqlite.getAll("chat", "=", chat, "uid", "messages");
        JSONArray array = new JSONArray();
        uids.forEach(o -> {
            boolean read = Boolean.getBoolean((String) sqlite.get("uid", "=", (String) o, "read", "messages"));
            if(!read) {
                String owner = (String) sqlite.get("uid", "=", (String) o, "owner", "messages");
                JSONObject message = new JSONObject();
                message.put("uid", o);
                message.put("message", sqlite.get("uid", "=", (String) o, "message", "messages"));
                message.put("owner", owner);
                message.put("read", read);
                message.put("time", SDF_MSG.format(new Date((long) sqlite.get("uid", "=", (String) o, "time", "messages"))) + " | " + (read ? "Visto" : "Entregue"));
                message.put("chat", chat);
                if (!owner.equalsIgnoreCase(user)) {
                    setRead((String) o, chat);
                }
                array.put(message);
            }
        });
        return array;
    }

    private ConditionValue[] messageCondition = new ConditionValue[]{
            new ConditionValue("uid", Conditional.EQUALS, "", Operator.NULL),
            new ConditionValue("chat", Conditional.EQUALS, "", Operator.AND)
    };

    public void setRead(String messageId, String chat) {
        sqlite.set(new ConditionModifier(messageCondition, messageId, chat).done(), "read", "true", "messages");
    }

    public String addMessage(String message, String owner, String chat) {
        UUID uid = UUID.randomUUID();
        sqlite.insert("uid, read, message, owner, chat, time", "'" + uid.toString() + "', 'read', '" + message + "', '" + owner + "', '" + chat + "', '" + System.currentTimeMillis() + "'", "messages");
        return uid.toString();
    }
}
