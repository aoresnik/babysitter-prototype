package xyz.aoresnik.babysitter.script;

import xyz.aoresnik.babysitter.entity.ScriptSource;

public class ScriptTypes {
    public static AbstractScriptType newForScriptSource(ScriptSource scriptSource) {
        if (scriptSource.getScriptSourceServerDir() != null) {
            return new ScriptTypeServerDir(scriptSource);
        } else if (scriptSource.getScriptSourceSSHDir() != null) {
            return new ScriptTypeSSHDir(scriptSource);
        } else {
            throw new RuntimeException("Unknown script source type for script source: " + scriptSource.getName() + " ID: " + scriptSource.getId() + ": No detail record present");
        }
    }
}
