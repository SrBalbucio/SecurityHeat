package com.securityheat.manager;

import balbucio.discordoauth.model.User;
import balbucio.sqlapi.model.ConditionModifier;
import balbucio.sqlapi.model.ConditionValue;
import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import com.securityheat.Main;

import java.util.Calendar;
import java.util.UUID;

public class UserManager {

    private Main instance;
    private HikariSQLiteInstance sqlite;

    public UserManager(Main instance) {
        this.instance = instance;
        this.sqlite = instance.getSqlite();
        sqlite.createTable("USERS", "uid VARCHAR(255) PRIMARY KEY, username VARCHAR(255), discordID VARCHAR(255), email VARCHAR(255), creationdate BIGINT, admin BOOLEAN");
    }

    private ConditionValue[] accountExist = new ConditionValue[]{
            new ConditionValue("discordID", ConditionValue.Conditional.EQUALS, "", ConditionValue.Operator.NULL)
    };

    public String createOrGetAccountID(User user){
        String discordID = user.getId();
        String email = user.getEmail();
        String uid;
        if(sqlite.exists(new ConditionModifier(accountExist, discordID).done(), "USERS")){
            uid = (String) sqlite.get(new ConditionModifier(accountExist, discordID).done(), "uid", "USERS");
        } else{
            uid = UUID.randomUUID().toString();
            sqlite.insert("uid, username, discordID, email, creationdate, admin", "'"+uid+"', '"+user.getUsername()+"', '"+discordID+"' ,'"+email+"', '"+ Calendar.getInstance().getTimeInMillis()+"', 'false'", "USERS");
            String chat = instance.getChatManager().createChat("Bem-Vindo", uid);
            instance.getChatManager().addMessage("Olá!" +
                    "Seja bem-vindo(a) a SecurityHeat, este é um canal para te auxiliar com a sua conta e dúvidas gerais. " +
                    "Se precisar de ajuda, é só dar um grito! " +
                    "Atenciosamente, Equipe de Suporte SecurityHeat!", "bot", chat);
        }
        if(discordID.equals("417356807669940224")){
            setAdmin(uid);
        }
        return uid;
    }

    public String getUsername(String uid){
        return (String) sqlite.get(new ConditionValue("uid", ConditionValue.Conditional.EQUALS, uid, ConditionValue.Operator.NULL), "username", "USERS");
    }

    public String getAccountID(String discordID){
        return (String) sqlite.get(new ConditionModifier(accountExist, discordID).done(), "uid", "USERS");
    }

    public void setAdmin(String uid){
        sqlite.set(new ConditionValue("uid", ConditionValue.Conditional.EQUALS, uid, ConditionValue.Operator.NULL), "admin", true, "USERS");
    }

    public void removeAdmin(String uid){
        sqlite.set("uid", "=", uid, "admin", false, "USERS");
    }

    public boolean isAdmin(String uid){
        return Boolean.getBoolean((String) sqlite.get("uid", "=", uid, "admin","USERS"));
    }
}
