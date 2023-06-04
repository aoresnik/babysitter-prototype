package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.CommandExecution;
import xyz.aoresnik.babysitter.entity.CommandSource;

import java.util.List;

abstract public class AbstractCommandType {

    private static final Logger log = Logger.getLogger(AbstractCommandType.class);

    private final CommandSource commandSource;

    public CommandSource getScriptSource() {
        return commandSource;
    }

    public AbstractCommandType(CommandSource commandSource) {

        this.commandSource = commandSource;
    }

    abstract public List<String> getScripts();

    abstract public AbstractCommandRunner createScriptExecution(String scriptName, String executionId);

    /**
     * Returns the script runner for the inactive script execution, from the database.
     * Runners for active sessions must be returned from {@link ActiveCommandRunners}.
     * TODO: support sessions running in tmux or equivalent, that can detach and reattach

     * @param commandExecution
     * @return
     */
    abstract public AbstractCommandRunner forInactiveScriptExecution(CommandExecution commandExecution);
}
