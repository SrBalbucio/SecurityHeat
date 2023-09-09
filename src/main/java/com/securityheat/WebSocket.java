package com.securityheat;

import balbucio.discordoauth.DiscordAPI;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.security.spec.ECField;

public class WebSocket extends WebSocketServer {

    private Main instance;

    public WebSocket(InetSocketAddress address, Main instance) {
        super(address);
        this.instance = instance;
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(org.java_websocket.WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(org.java_websocket.WebSocket webSocket, String s) {
        try {
            JSONObject json = new JSONObject(s);
            if(json.has("type")){
                String type = json.getString("type");
                if(type.equalsIgnoreCase("NEWSLETTER")){
                    webSocket.send(new JSONObject()
                            .put("type", "NEWSLETTER")
                            .put("message", "Seu email foi registrado na nossa Newsletter, agradecemos imensamente o seu interesse!")
                            .toString());
                    instance.getNewsletterManager().registerInNewsletter(json.getString("email"));
                } else if(type.equalsIgnoreCase("LOGIN")){
                    webSocket.send(new JSONObject()
                            .put("type", "LOGIN_URL")
                            .put("url", instance.getDiscordProvider().getURL(json.getInt("value")))
                            .toString());
                } else if(type.equalsIgnoreCase("CODE")){
                    try{
                        DiscordAPI dc = instance.getDiscordProvider().getAPI(json.getString("code"));
                        webSocket.send(new JSONObject()
                                .put("type", "LOGIN")
                                .put("value", instance.getUserManager().createOrGetAccountID(dc.fetchUser()))
                                .toString());
                        webSocket.send(new JSONObject()
                                .put("type", "ACTION")
                                .put("action", 0)
                                .toString());
                    } catch (Exception e){
                        e.printStackTrace();
                        webSocket.send(new JSONObject()
                                .put("type", "ERRO")
                                .put("value", "Não foi possível fazer login usando sua conta Discord!")
                                .toString());
                    }
                } else if(type.equalsIgnoreCase("NEWCHAT")){
                    String id = json.getString("user");
                    int action = json.getInt("action");
                    String chatid = "null";
                    switch (action) {
                        case 1: {
                            chatid = instance.getChatManager().createChat("Suporte", id);
                            instance.getChatManager().addMessage("Olá, no que podemos te ajudar hoje?", "bot", chatid);
                            break;
                        }
                        case 10: {
                            chatid = instance.getChatManager().createChat("Pedido de Orçamento: ADC", id);
                            instance.getChatManager().addMessage("Queria fazer um orçamento de Análise de Conversão.", id, chatid);
                            break;
                        }
                        case 11: {
                            chatid = instance.getChatManager().createChat("Pedido de Orçamento: ADS", id);
                            instance.getChatManager().addMessage("Queria fazer um orçamento de Análise de Segurança.", id, chatid);
                            break;
                        }
                        case 12: {
                            chatid = instance.getChatManager().createChat("Pedido de Orçamento: APP", id);
                            instance.getChatManager().addMessage("Queria fazer um orçamento de Criação de Aplicativos.", id, chatid);
                            break;
                        }
                        case 13: {
                            chatid = instance.getChatManager().createChat("Novo Pedido: HOST", id);
                            instance.getChatManager().addMessage("Queria assinar um plano de Hospedagem.", id, chatid);
                            break;
                        }
                        case 14: {
                            chatid = instance.getChatManager().createChat("Pedido de Orçamento: Manutenção", id);
                            instance.getChatManager().addMessage("Queria fazer um orçamento de Manutenção.", id, chatid);
                            break;
                        }
                        case 15: {
                            chatid = instance.getChatManager().createChat("Novo Pedido: Registro de Domínio", id);
                            instance.getChatManager().addMessage("Queria fazer o registro de um domínio.", id, chatid);
                            break;
                        }
                        case 16: {
                            chatid = instance.getChatManager().createChat("Pedido de Orçamento: Site", id);
                            instance.getChatManager().addMessage("Queria fazer um orçamento de Site.", id, chatid);
                            break;
                        }
                    }
                    webSocket.send(new JSONObject()
                            .put("type", "CHATUPDATE")
                            .put("chatid", chatid)
                            .toString());
                } else if(type.equalsIgnoreCase("CHECKADMIN")){
                    webSocket.send(new JSONObject()
                            .put("type", "ADMIN")
                            .put("is", instance.getUserManager().isAdmin(json.getString("uid")))
                            .toString());
                } else if(type.equalsIgnoreCase("UPDATECHAT")){
                    webSocket.send(new JSONObject()
                            .put("type", "CHATLIST")
                            .put("chats", instance.getChatManager().getChats(json.getString("uid")))
                            .toString());
                } else if(type.equalsIgnoreCase("UPDATEUNREADMESSAGES")){
                    if(json.get("chat") != JSONObject.NULL) {
                        webSocket.send(new JSONObject()
                                .put("type", "UNREAD")
                                .put("messages", instance.getChatManager().getUnreadMessages(json.getString("chat"), json.getString("user")))
                                .toString());
                    }
                }else if(type.equalsIgnoreCase("LOADMESSAGES")){
                    if(json.get("chat") != JSONObject.NULL) {
                        webSocket.send(new JSONObject()
                                .put("type", "MESSAGES")
                                .put("messages", instance.getChatManager().getMessages(json.getString("chat"), json.getString("user")))
                                .toString());
                    }
                } else if(type.equalsIgnoreCase("SENDMSG")){
                    instance.getChatManager().addMessage(json.getString("message"), json.getString("user"), json.getString("uid"));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onError(org.java_websocket.WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
