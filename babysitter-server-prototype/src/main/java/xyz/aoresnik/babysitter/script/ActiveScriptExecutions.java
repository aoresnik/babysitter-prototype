package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ActiveScriptExecutions {
    @Inject
    Logger log;

    private Map<String, ScriptExecution> scriptExecutions = new ConcurrentHashMap<>();

    public void addScriptExecution(ScriptExecution scriptExecution) {
        log.info("Registering active script execution: " + scriptExecution.getSessionId());
        scriptExecutions.put(scriptExecution.getSessionId(), scriptExecution);
    }

    public void removeScriptExecution(String sessionId) {
        log.info("Removing active script execution: " + sessionId);
        scriptExecutions.remove(sessionId);
    }
}
