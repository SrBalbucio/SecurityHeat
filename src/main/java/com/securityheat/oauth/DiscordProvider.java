package com.securityheat.oauth;

import balbucio.discordoauth.DiscordAPI;
import balbucio.discordoauth.DiscordOAuth;
import balbucio.discordoauth.model.TokensResponse;
import balbucio.discordoauth.scope.SupportedScopes;
import com.securityheat.Main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiscordProvider {

    private DiscordOAuth oauth;
    private Map<String, Integer> states = new HashMap<>();

    public DiscordProvider(){
        this.oauth = new DiscordOAuth(
                "1149377444298620968",
                "tWQtB29xDKGzXT9LegzY8EawNMD3-e7m",
                Main.TEST ? "http://localhost:63342/SecurityHeat/public/index.html" : "https://securityheat.com/index.html",
                new SupportedScopes[] { SupportedScopes.IDENTIFY, SupportedScopes.IDENTIFY_EMAIL, SupportedScopes.GUILDS_JOIN});
    }

    public String getURL(Integer action){
        return oauth.getAuthorizationURL("A"+action);
    }

    public DiscordAPI getAPI(String code) throws IOException {
        TokensResponse response = oauth.getTokens(code);
        DiscordAPI dc = new DiscordAPI(response.getAccessToken());
        return dc;
    }

}
