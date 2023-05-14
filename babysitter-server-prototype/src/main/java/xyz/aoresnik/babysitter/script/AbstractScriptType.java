package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.io.IOException;
import java.util.List;

abstract public class AbstractScriptType {

    private static final Logger log = Logger.getLogger(AbstractScriptType.class);

    private final ScriptSource scriptSource;

    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    public AbstractScriptType(ScriptSource scriptSource) {

        this.scriptSource = scriptSource;
    }

    abstract public List<String> getScripts();

    public AbstractScriptExecution createScriptExecution(String scriptName) {
        try {
            return new AbstractScriptExecution(scriptSource, scriptName);
        } catch (IOException e) {
            throw new RuntimeException("Internal error while attempting to run script", e);
        }
    }
}
