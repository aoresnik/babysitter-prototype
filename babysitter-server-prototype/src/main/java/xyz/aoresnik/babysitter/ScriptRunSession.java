package xyz.aoresnik.babysitter;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Based on https://quarkus.io/guides/websockets
 */
@ServerEndpoint("/api/v1/scripts/{scriptName}/session/{sessionId}/websocket")
@ApplicationScoped
public class ScriptRunSession {

    @Inject
    Logger log;

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("scriptName") String scriptName, @PathParam("sessionId") String sessionId) {
        sessions.put(scriptName, session);
        log.debug("Connected terminal for script " + scriptName);
    }

    @OnClose
    public void onClose(Session session, @PathParam("scriptName") String scriptName, @PathParam("sessionId") String sessionId) {
        sessions.remove(scriptName);
        broadcast("Disconnected terminal for script " + scriptName);
    }

    @OnError
    public void onError(Session session, @PathParam("scriptName") String scriptName, Throwable throwable) {
        sessions.remove(scriptName);
        log.debug("Terminal for " + scriptName + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("scriptName") String scriptName, @PathParam("sessionId") String sessionId) {
        log.debug("Received message: " + message);
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

}
