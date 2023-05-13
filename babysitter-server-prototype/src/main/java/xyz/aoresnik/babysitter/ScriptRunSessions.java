package xyz.aoresnik.babysitter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.ScriptExecutionData;
import xyz.aoresnik.babysitter.data.ScriptExecutionInitialStateData;
import xyz.aoresnik.babysitter.data.ScriptInputData;
import xyz.aoresnik.babysitter.script.ScriptExecution;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Based on https://quarkus.io/guides/websockets
 */
@ServerEndpoint(value = "/api/v1/scripts/session/{sessionId}/websocket", encoders = {ScriptRunSessions.EncoderDecoder.class}, decoders = {ScriptRunSessions.EncoderDecoder.class})
@ApplicationScoped
public class ScriptRunSessions {

    @Inject
    Logger log;

    Map<String, ScriptRunSession> sessions = new ConcurrentHashMap<>();

    static class ScriptRunSession {
        String scriptName;
        private final ScriptExecution scriptExecution;
        Session websocketSession;

        Consumer<ScriptExecutionData> listener;

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
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) {
        ScriptRunSession scriptRunSession = sessions.get(sessionId);
        scriptRunSession.setWebsocketSession(session);
        log.debug("Connected terminal for script execution session ID: " + sessionId);
        ScriptExecution scriptExecution = scriptRunSession.getScriptExecution();

        Consumer<ScriptExecutionData> listener = new Consumer<ScriptExecutionData>() {
            @Override
            public void accept(ScriptExecutionData scriptExecutionData) {
                try {
                    StringWriter writer = new StringWriter();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(writer, scriptExecutionData);
                    String json = writer.toString();
                    session.getAsyncRemote().sendText(json, result ->  {
                        if (result.getException() != null) {
                            log.error("Unable to send message: " + result.getException());
                        }
                    });
                } catch (IOException e) {
                    log.error("Unable to serialize script execution data: " + e);
                }
            }
        };
        scriptRunSession.listener = listener;
        ScriptExecutionInitialStateData initialStateData = scriptExecution.registerListener(listener);
        session.getAsyncRemote().sendObject(initialStateData, result ->  {
            if (result.getException() != null) {
                log.error("Unable to send message: " + result.getException());
            }
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("sessionId") String sessionId) {
        ScriptRunSession scriptRunSession = sessions.get(sessionId);
        ScriptExecution scriptExecution = scriptRunSession.getScriptExecution();
        if (scriptExecution != null)
        {
            scriptExecution.removeListener(scriptRunSession.listener);
        }
        sessions.remove(sessionId);
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("sessionId") String sessionId) {
        sessions.remove(sessionId);
        log.debug("Terminal for execution session ID " + sessionId + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(ScriptInputData message, @PathParam("sessionId") String sessionId) {
        log.debug("Received message: " + message);
        ScriptRunSession scriptRunSession = sessions.get(sessionId);
        ScriptExecution scriptExecution = scriptRunSession.getScriptExecution();
        scriptExecution.sendInput(message);
    }

    public static class EncoderDecoder implements javax.websocket.Encoder.Text<ScriptExecutionInitialStateData>, javax.websocket.Decoder.Text<ScriptInputData> {
        ObjectMapper mapper;

        Logger log = Logger.getLogger(EncoderDecoder.class);

        public EncoderDecoder() {
        }

        @Override
        public void init(EndpointConfig config) {
            mapper = new ObjectMapper();
        }

        @Override
        public String encode(ScriptExecutionInitialStateData object) throws EncodeException {
            try {
                StringWriter sw = new StringWriter();
                mapper.writeValue(sw, object);
                return sw.toString();
            } catch (IOException e) {
                throw new RuntimeException("Unable to serialize to JSON", e);
            }
        }

        @Override
        public ScriptInputData decode(String s) throws DecodeException {
            try {
                log.debug("Decoding " + s);
                return mapper.readValue(new StringReader(s), ScriptInputData.class);
            } catch (StreamReadException e) {
                throw new RuntimeException(e);
            } catch (DatabindException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean willDecode(String s) {
            return s != null;
        }

        @Override
        public void destroy() {

        }
    }
}
