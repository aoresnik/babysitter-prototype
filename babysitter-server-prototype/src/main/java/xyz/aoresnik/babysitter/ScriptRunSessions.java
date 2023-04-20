package xyz.aoresnik.babysitter;

import io.quarkus.scheduler.Scheduled;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Based on https://quarkus.io/guides/websockets
 */
@ServerEndpoint("/api/v1/scripts/{scriptName}/session/{sessionId}/websocket")
@ApplicationScoped
public class ScriptRunSessions {

    @Inject
    Logger log;

    Map<String, ScriptRunSession> sessions = new ConcurrentHashMap<>();

    class ScriptRunSession {
        String scriptName;
        String sessionId;
        Session websocketSession;

        public ScriptRunSession(String scriptName, String sessionId) {
            this.scriptName = scriptName;
            this.sessionId = sessionId;
        }

        public String getScriptName() {
            return scriptName;
        }

        /**
         * Script run session ID. Not to be confused with @link websocketSession.getId()
         * @return
         */
        public String getSessionId() {
            return sessionId;
        }

        public Session getWebsocketSession() {
            return websocketSession;
        }

        public void setWebsocketSession(Session websocketSession) {
            this.websocketSession = websocketSession;
        }
    }

    public ScriptRunSession getScriptRunSession(String scriptName) {
        String sessionId = UUID.randomUUID().toString();
        ScriptRunSession scriptRunSession = new ScriptRunSession(scriptName, sessionId);
        sessions.put(sessionId, scriptRunSession);
        log.debug("Created new script run session for script " + scriptName + " with session ID: " + sessionId);
        return scriptRunSession;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("scriptName") String scriptName, @PathParam("sessionId") String sessionId) {
        ScriptRunSession scriptRunSession = sessions.get(sessionId);
        scriptRunSession.setWebsocketSession(session);
        log.debug("Connected terminal for script " + scriptName + " with session ID: " + sessionId);
        session.getAsyncRemote().sendObject("Test", result ->  {
            if (result.getException() != null) {
                log.error("Unable to send message: " + result.getException());
            }
        });
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

    @Scheduled(every = "5s")
    void increment() {
        if (sessions != null) {
            broadcast("Test");
        }
    }

    private void broadcast(String message) {
        log.info("Broadcasting message: " + message);
        sessions.values().forEach(s -> {
            if (s.getWebsocketSession() != null) {
                log.debug("Sending message to script run session ID: " + s.getSessionId() + " session ID: " + s.getWebsocketSession().getId());
                s.getWebsocketSession().getAsyncRemote().sendObject(message, result -> {
                    if (result.getException() != null) {
                        log.error("Unable to send message: " + result.getException());
                    }
                });
            } else {
                log.warn("No websocket session for scriptr run session ID: " + s.getSessionId());
            }
        });
    }

}
