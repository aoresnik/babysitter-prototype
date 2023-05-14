package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
}
