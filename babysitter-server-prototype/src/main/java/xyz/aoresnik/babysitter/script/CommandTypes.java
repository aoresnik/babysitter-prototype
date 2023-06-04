package xyz.aoresnik.babysitter.script;

import xyz.aoresnik.babysitter.entity.CommandSource;

public class CommandTypes {
    public static AbstractCommandType newForScriptSource(CommandSource commandSource) {
        if (commandSource.getScriptSourceServerDir() != null) {
            return new CommandTypeServerDir(commandSource);
        } else if (commandSource.getScriptSourceSSHDir() != null) {
            return new CommandTypeSSHDir(commandSource);
        } else {
            throw new RuntimeException("Unknown script source type for script source: " + commandSource.getName() + " ID: " + commandSource.getId() + ": No detail record present");
        }
    }
}
