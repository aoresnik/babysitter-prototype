package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ActiveScriptRunners {
    @Inject
    Logger log;

    private Map<String, AbstractScriptRunner> scriptRunners = new ConcurrentHashMap<>();

    public void addScriptExecution(AbstractScriptRunner scriptExecution) {
        log.info("Registering active script execution: " + scriptExecution.getScriptExecutionID());
        scriptRunners.put(scriptExecution.getScriptExecutionID(), scriptExecution);
    }

    public void removeScriptExecution(String sessionId) {
        log.info("Removing active script execution: " + sessionId);
        scriptRunners.remove(sessionId);
    }

    public AbstractScriptRunner getScriptExecution(String sessionId) {
        return scriptRunners.get(sessionId);
    }
}
