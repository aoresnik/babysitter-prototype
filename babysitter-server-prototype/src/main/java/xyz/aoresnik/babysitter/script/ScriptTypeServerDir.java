package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptTypeServerDir extends AbstractScriptType {

    private static final Logger log = Logger.getLogger(ScriptTypeServerDir.class);

    public ScriptTypeServerDir(ScriptSource scriptSource) {
        super(scriptSource);
    }

    public List<String> getScripts() {
        String currentPath = null;
        try {
            currentPath = new File(".").getCanonicalPath();
            log.info("Current dir: " + currentPath + " NOTE: Quarkus runs in classes/java/main, so the scripts are in ../../..");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String dirName = getScriptSource().getScriptSourceServerDir().getDirname();
        log.debug(String.format("Reading scripts from dir: %s", dirName));
        File scriptsDir = new File(currentPath, dirName);
        File[] files = scriptsDir.listFiles(file -> file.getName().endsWith(".sh"));
        if (files == null) {
            throw new RuntimeException("Invalid scripts dir in configuration: " + scriptsDir);
        }
        List<String> filenamesList = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        return filenamesList;
    }

    @Override
    public AbstractScriptExecution createScriptExecution(String scriptName)  {
        return new LocalScriptTypeExecution(getScriptSource(), scriptName);
    }

    class LocalScriptTypeExecution extends AbstractScriptExecution {

        public LocalScriptTypeExecution(ScriptSource scriptSource, String scriptName) {
            super(scriptSource, scriptName);
        }
    }
}
