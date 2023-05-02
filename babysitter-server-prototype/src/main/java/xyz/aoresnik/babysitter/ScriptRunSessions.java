package xyz.aoresnik.babysitter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.ScriptExecutionInitialStateData;
import xyz.aoresnik.babysitter.script.ScriptExecution;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Based on https://quarkus.io/guides/websockets
 */
@ServerEndpoint(value = "/api/v1/scripts/{scriptName}/session/{sessionId}/websocket", encoders = {ScriptRunSessions.EncoderDecoder.class}, decoders = {ScriptRunSessions.EncoderDecoder.class})
@ApplicationScoped
public class ScriptRunSessions {

    @Inject
    Logger log;

    Map<String, ScriptRunSession> sessions = new ConcurrentHashMap<>();

    static class ScriptRunSession {
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
        ScriptExecution scriptExecution = scriptRunSession.getScriptExecution();
        String resultText = scriptExecution.getResult().stream().collect(Collectors.joining("\n"));
        ScriptExecutionInitialStateData initialStateData = new ScriptExecutionInitialStateData();

        initialStateData.setScriptRun(true);
        initialStateData.setScriptCompleted(true);
        initialStateData.setExitCode(scriptExecution.getExitCode());
        initialStateData.setErrorText(scriptExecution.getErrorText());
        initialStateData.setInitialConsoleData(resultText.getBytes(StandardCharsets.UTF_8));

        session.getAsyncRemote().sendObject(initialStateData, result ->  {
            if (result.getException() != null) {
                log.error("Unable to send message: " + result.getException());
            }
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("scriptName") String scriptName, @PathParam("sessionId") String sessionId) {
        sessions.remove(scriptName);
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

    public static class EncoderDecoder implements javax.websocket.Encoder.Text<ScriptExecutionInitialStateData>, javax.websocket.Decoder.Text<ScriptExecutionInitialStateData> {
        ObjectMapper mapper;

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
        public ScriptExecutionInitialStateData decode(String s) throws DecodeException {
            try {
                return mapper.readValue(new StringReader(s), ScriptExecutionInitialStateData.class);
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
