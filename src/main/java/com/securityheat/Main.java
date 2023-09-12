package com.securityheat;

import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import balbucio.sqlapi.sqlite.SqliteConfig;
import com.securityheat.manager.*;
import com.securityheat.oauth.DiscordProvider;
import lombok.Data;

import java.io.*;
import java.net.InetSocketAddress;

@Data
public class Main {

    public static boolean TEST = true;
    public static void main(String[] args) {
        new Main();
    }

    private WebSocket server;
    private HikariSQLiteInstance sqlite;
    private DiscordProvider discordProvider;
    private NewsletterManager newsletterManager;
    private UserManager userManager;
    private ChatManager chatManager;
    private ActionManager actionManager;
    private CommandManager commandManager;

    public Main(){
        SqliteConfig sqliteConfig = new SqliteConfig(new File("database.db"));
        sqliteConfig.createFile();
        this.sqlite = new HikariSQLiteInstance(sqliteConfig);
        this.discordProvider = new DiscordProvider();
        this.newsletterManager = new NewsletterManager(this);
        this.userManager = new UserManager(this);
        this.chatManager = new ChatManager(this);
        this.actionManager = new ActionManager(this);
        this.commandManager = new CommandManager(this);
        this.server = new WebSocket(new InetSocketAddress("127.0.0.1", 25465), this);
        server.run();
    }
}