package xyz.aoresnik.babysitter;

import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.script.ScriptExecution;

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
import java.util.stream.Collectors;

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
        private final ScriptExecution scriptExecution;
        Session websocketSession;

        public ScriptRunSession(String scriptName, ScriptExecution scriptExecution) {
            this.scriptName = scriptName;
            this.scriptExecution = scriptExecution;
        }

        public String getScriptName() {
            return scriptName;
        }

        public ScriptExecution getScriptExecution() {
            return scriptExecution;
        }

        public Session getWebsocketSession() {
            return websocketSession;
        }

        public void setWebsocketSession(Session websocketSession) {
            this.websocketSession = websocketSession;
        }
    }

    public ScriptRunSession createForActiveExecution(String scriptName, ScriptExecution scriptExecution) {
        ScriptRunSession scriptRunSession = new ScriptRunSession(scriptName, scriptExecution);
        sessions.put(scriptExecution.getSessionId(), scriptRunSession);
        log.debug("Created new script run session for script " + scriptName + " with session run ID: " + scriptExecution.getSessionId());
        return scriptRunSession;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("scriptName") String scriptName, @PathParam("sessionId") String sessionId) {
        ScriptRunSession scriptRunSession = sessions.get(sessionId);
        scriptRunSession.setWebsocketSession(session);
        log.debug("Connected terminal for script " + scriptName + " with session ID: " + sessionId);
        session.getAsyncRemote().sendObject(scriptRunSession.getScriptExecution().getResult().stream().collect(Collectors.joining("\n")), result ->  {
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
//        if (sessions != null) {
//            broadcast("Test");
//        }
    }

    private void broadcast(String message) {
        log.info("Broadcasting message: " + message);
        sessions.values().forEach(s -> {
            if (s.getWebsocketSession() != null) {
                log.debug("Sending message to script run session ID: " + s.getScriptExecution().getSessionId() + " session ID: " + s.getWebsocketSession().getId());
                s.getWebsocketSession().getAsyncRemote().sendObject(message, result -> {
                    if (result.getException() != null) {
                        log.error("Unable to send message: " + result.getException());
                    }
                });
            } else {
                log.warn("No websocket session for script run session ID: " + s.getScriptExecution().getSessionId());
            }
        });
    }

}
