package xyz.aoresnik.babysitter.script;

import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static xyz.aoresnik.babysitter.ScriptsResource.SCRIPTS_DIR;

/**
 * TODO: simple synchronous implementation, implement async
 */
public class ScriptExecution {

    Logger log = Logger.getLogger(ScriptExecution.class);

    private final String scriptName;

    private List<String> result;

    public ScriptExecution(String scriptName) {

        this.scriptName = scriptName;
    }

    public void start() {
        File scriptsDir = SCRIPTS_DIR.toFile();

        try {
            ProcessBuilder pb = new ProcessBuilder(new File(scriptsDir, scriptName).getCanonicalPath());

            File stdoutLog = File.createTempFile("stdout-log-", "txt");
            Map<String, String> env = pb.environment();
            pb.directory(scriptsDir);
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(stdoutLog));
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
            }
            List<String> result = Files.readAllLines(stdoutLog.toPath());
            log.debug("Result: " + result);
            stdoutLog.delete();
            this.result = result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitFor() {
        // For now, entire execution is in start
    }

    public List<String> getResult() {
        return result;
    }
}
