package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.util.ArrayList;
import java.util.List;

public class ScriptTypeSSH extends AbstractScriptType {

    private static final Logger log = Logger.getLogger(ScriptTypeLocal.class);

    public ScriptTypeSSH(ScriptSource scriptSource) {
        super(scriptSource);
    }

    @Override
    public List<String> getScripts() {
        log.error("SSH script source script enumeration not yet implemented");
        return new ArrayList<>();
    }
}
