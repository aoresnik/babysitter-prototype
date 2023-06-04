package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.util.List;

abstract public class AbstractCommandType {

    private static final Logger log = Logger.getLogger(AbstractCommandType.class);

    private final ScriptSource scriptSource;

    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    public AbstractCommandType(ScriptSource scriptSource) {

        this.scriptSource = scriptSource;
    }

    abstract public List<String> getScripts();

    abstract public AbstractCommandRunner createScriptExecution(String scriptName, String executionId);

    /**
     * Returns the script runner for the inactive script execution, from the database.
     * Runners for active sessions must be returned from {@link ActiveCommandRunners}.
     * TODO: support sessions running in tmux or equivalent, that can detach and reattach

     * @param scriptExecution
     * @return
     */
    abstract public AbstractCommandRunner forInactiveScriptExecution(ScriptExecution scriptExecution);
}
