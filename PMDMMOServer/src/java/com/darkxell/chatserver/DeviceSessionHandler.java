/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.darkxell.chatserver;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;
import java.io.IOException;
import javax.json.JsonObject;

@ApplicationScoped
public class DeviceSessionHandler {
    
    private final Set<Session> sessions = new HashSet<>();
    
     public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public void sendToAllConnectedSessions(JsonObject message) {
        sessions.forEach((session) -> {
            sendToSession(session, message);
        });
    }

    public void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            System.err.println("Error while sending a message");
        }
    }
}