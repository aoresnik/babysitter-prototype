package xyz.aoresnik.babysitter.script;

import xyz.aoresnik.babysitter.entity.ScriptSource;

public class ScriptTypes {
    public static AbstractScriptType forScriptSource(ScriptSource scriptSource) {
        if (scriptSource.getScriptSourceServerDir() != null) {
            return new ScriptTypeLocal(scriptSource);
        } else if (scriptSource.getScriptSourceSSHDir() != null) {
            return new ScriptTypeSSH(scriptSource);
        } else {
            throw new RuntimeException("Unknown script source type for script source: " + scriptSource.getName() + " ID: " + scriptSource.getId() + ": No detail record present");
        }
    }
}
