package xyz.aoresnik.babysitter.script;

import lombok.Getter;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static xyz.aoresnik.babysitter.ScriptsResource.SCRIPTS_DIR;

/**
 * TODO: simple synchronous implementation, implement async
 */
public class ScriptExecution {

    @Getter
    private final String sessionId;

    Logger log = Logger.getLogger(ScriptExecution.class);

    @Getter
    private final String scriptName;

    @Getter
    private String errorText;

    @Getter
    private Integer exitCode;

    public ScriptExecution(String scriptName) throws IOException {
        this.scriptName = scriptName;
        this.sessionId = UUID.randomUUID().toString();
        getStdoutFile().createNewFile();
    }

    public void start() {
        File scriptsDir = SCRIPTS_DIR.toFile();

        try {
            ProcessBuilder pb = new ProcessBuilder(new File(scriptsDir, scriptName).getCanonicalPath());
            File stdoutLog = getStdoutFile();
            Map<String, String> env = pb.environment();
            pb.directory(scriptsDir);
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(stdoutLog));

            Process p = pb.start();
            try {
                this.exitCode = p.waitFor();
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
            log.error("Error running script", e);
            errorText = e.getMessage();

        }
    }

    public List<String> getResult() {
        List<String> result = null;
        try {
            result = Files.readAllLines(getStdoutFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read result from file", e);
        }
        log.debug("Result: " + result);
        return result;
    }

    private File getStdoutFile() {
        return new File(System.getProperty("java.io.tmpdir"), "stdout-" + sessionId + ".log");
    }

    public void waitFor() {
        // For now, entire execution is in start
    }
}
