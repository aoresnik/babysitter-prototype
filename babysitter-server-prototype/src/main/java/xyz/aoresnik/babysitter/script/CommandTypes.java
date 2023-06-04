package xyz.aoresnik.babysitter.script;

import xyz.aoresnik.babysitter.entity.ScriptSource;

public class CommandTypes {
    public static AbstractCommandType newForScriptSource(ScriptSource scriptSource) {
        if (scriptSource.getScriptSourceServerDir() != null) {
            return new CommandTypeServerDir(scriptSource);
        } else if (scriptSource.getScriptSourceSSHDir() != null) {
            return new CommandTypeSSHDir(scriptSource);
        } else {
            throw new RuntimeException("Unknown script source type for script source: " + scriptSource.getName() + " ID: " + scriptSource.getId() + ": No detail record present");
        }
    }
}
