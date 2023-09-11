package com.securityheat.manager;

import balbucio.discordoauth.model.User;
import balbucio.sqlapi.model.ConditionModifier;
import balbucio.sqlapi.model.ConditionValue;
import balbucio.sqlapi.model.Conditional;
import balbucio.sqlapi.model.Operator;
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
            new ConditionValue("discordID", Conditional.EQUALS, "", Operator.NULL)
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
            String chat = instance.getChatManager().createChat("Central da Comunidade", uid, "logo-white.png", "updatechannel");
            instance.getChatManager().addMessage("Olá!<br>" +
                    "Seja bem-vindo(a) a SecurityHeat, estamos entusiasmados em vê-lo por aqui!<br>" +
                    "Esta é a página onde você pode tirar dúvidas, fazer orçamentos, assinar planos e muito mais, sempre será um prazer atendê-lo. <br>" +
                    "Para que nossa convivência seja melhor ainda, é importante que você se lembre de manter a educação e o respeito em primeiro lugar.<br>" +
                    "Nosso atendimento geral funciona de terça a quinta apartir das 8h, apenas nosso atendimento técnico funciona 24/7 (ele resolve apenas problemas na infraestrutura e hospedagem).<br>" +
                    "<br>" +
                    "Atenciosamente, Equipe de Suporte SecurityHeat!", "bot", chat);
        }
        if(discordID.equalsIgnoreCase("417356807669940224")){
            setAdmin(uid);
        }
        return uid;
    }

    public String getUsername(String uid){
        return (String) sqlite.get(new ConditionValue("uid", Conditional.EQUALS, uid, Operator.NULL), "username", "USERS");
    }

    public String getAccountID(String discordID){
        return (String) sqlite.get(new ConditionModifier(accountExist, discordID).done(), "uid", "USERS");
    }

    public void setAdmin(String uid){
        sqlite.update("UPDATE USERS SET 'admin' = 'true' WHERE uid = '"+uid+"';");
    }

    public void removeAdmin(String uid){
        sqlite.set("uid", "=", uid, "admin", "false", "USERS");
    }

    public boolean isAdmin(String uid){
        return Boolean.parseBoolean((String) sqlite.get("uid", "=", uid, "admin","USERS"));
    }
}
