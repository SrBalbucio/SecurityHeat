package com.securityheat.manager;

import balbucio.sqlapi.model.ConditionModifier;
import balbucio.sqlapi.model.ConditionValue;
import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import com.securityheat.Main;
import org.checkerframework.checker.units.qual.N;

public class NewsletterManager {

    private Main instance;
    private HikariSQLiteInstance sqlite;

    public NewsletterManager(Main instance){
        this.instance = instance;
        this.sqlite = instance.getSqlite();
        sqlite.createTable("newsletter", "email VARCHAR(255) NOT NULL");
    }

    private ConditionValue[] condition = new ConditionValue[]{
            new ConditionValue("email", ConditionValue.Conditional.EQUALS, "", ConditionValue.Operator.NULL)
    };

    public void registerInNewsletter(String email){
        if(!sqlite.exists(new ConditionModifier(condition, email).done(), "newsletter")) {
            sqlite.insert("email", "'" + email + "'", "newsletter");
        }
    }
}
