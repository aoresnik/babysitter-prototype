package xyz.aoresnik.babysitter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.AbstractScriptExecutionRTData;
import xyz.aoresnik.babysitter.data.ScriptExecutionInitialStateRTData;
import xyz.aoresnik.babysitter.data.ScriptInputData;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.script.AbstractScriptRunner;
import xyz.aoresnik.babysitter.script.ActiveScriptRunners;
import xyz.aoresnik.babysitter.script.ScriptTypes;

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
 * Provides a two-way exchange of data between server and client for active script executions. Sends console updates to
 * the client and receives input from the client.
 *
 * Based on https://quarkus.io/guides/websockets
 */
@ServerEndpoint(value = "/api/v1/commands/session/{sessionId}/websocket", encoders = {ScriptRunSessions.EncoderDecoder.class}, decoders = {ScriptRunSessions.EncoderDecoder.class})
@ApplicationScoped
public class ScriptRunSessions {

    @Inject
    Logger log;

    /**
     * TODO: persistent somewhere
     */
    Map<String, ScriptRunSession> sessions = new ConcurrentHashMap<>();

    @Inject
    ActiveScriptRunners activeScriptRunners;

    @Inject
    ScriptExecutionService scriptExecutionService;

    static class ScriptRunSession {
        Consumer<AbstractScriptExecutionRTData> listener;


    }

    public void createForActiveExecution(String scriptName, AbstractScriptRunner scriptRunner) {
        activeScriptRunners.addScriptExecution(scriptRunner);
        log.debug("Created new script run session for script " + scriptName + " with session run ID: " + scriptRunner.getScriptExecutionID());
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) {
        ScriptRunSession scriptRunSession = new ScriptRunSession();
        sessions.put(sessionId, scriptRunSession);
        log.debug("Connected terminal for script execution session ID: " + sessionId);

        Consumer<AbstractScriptExecutionRTData> listener = new Consumer<AbstractScriptExecutionRTData>() {
            @Override
            public void accept(AbstractScriptExecutionRTData scriptExecutionData) {
                try {
                    StringWriter writer = new StringWriter();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(writer, scriptExecutionData);
                    String json = writer.toString();
                    session.getAsyncRemote().sendText(json, result -> {
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
        AbstractScriptRunner scriptRunner = activeScriptRunners.getScriptExecution(sessionId);
        if (scriptRunner != null) {
            log.error("For session ID=%s the script runner is still active".formatted(sessionId));
            ScriptExecutionInitialStateRTData initialStateData = scriptRunner.registerConsoleChangeListener(listener);
            session.getAsyncRemote().sendObject(initialStateData, result -> {
                if (result.getException() != null) {
                    log.error("Unable to send message: " + result.getException());
                }
            });
        } else {
            log.error("For session ID=%s the script runner has completed and is inactive - obtaining from database".formatted(sessionId));
            ScriptExecution scriptExecution = scriptExecutionService.getScriptExecution(sessionId);
            AbstractScriptRunner scriptRunner1 = ScriptTypes.newForScriptSource(scriptExecution.getScriptSource()).forInactiveScriptExecution(scriptExecution);
            ScriptExecutionInitialStateRTData initialStateData = scriptRunner1.getScriptExecutionInitialStateData();
            session.getAsyncRemote().sendObject(initialStateData, result -> {
                if (result.getException() != null) {
                    log.error("Unable to send message: " + result.getException());
                }
            });
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("sessionId") String sessionId) {
        ScriptRunSession scriptRunSession = sessions.get(sessionId);
        AbstractScriptRunner scriptExecution = activeScriptRunners.getScriptExecution(sessionId);
        if (scriptExecution != null)
        {
            scriptExecution.removeConsoleChangeListener(scriptRunSession.listener);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("sessionId") String sessionId) {
        log.debug("Terminal for execution session ID " + sessionId + " left on error: ", throwable);
    }

    @OnMessage
    public void onMessage(ScriptInputData message, @PathParam("sessionId") String sessionId) {
        log.debug("Received message: " + message);
        ScriptRunSession scriptRunSession = sessions.get(sessionId);
        AbstractScriptRunner scriptExecution = activeScriptRunners.getScriptExecution(sessionId);
        if (scriptExecution != null) {
            scriptExecution.sendInput(message);
        }
    }

    public static class EncoderDecoder implements javax.websocket.Encoder.Text<ScriptExecutionInitialStateRTData>, javax.websocket.Decoder.Text<ScriptInputData> {
        ObjectMapper mapper;

        Logger log = Logger.getLogger(EncoderDecoder.class);

        public EncoderDecoder() {
        }

        @Override
        public void init(EndpointConfig config) {
            mapper = new ObjectMapper();
        }

        @Override
        public String encode(ScriptExecutionInitialStateRTData object) throws EncodeException {
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
