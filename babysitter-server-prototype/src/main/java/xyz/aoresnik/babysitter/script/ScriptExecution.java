package xyz.aoresnik.babysitter.script;

import lombok.Getter;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        try {

            File scriptsDir = SCRIPTS_DIR.toFile();
            ProcessBuilder pb = new ProcessBuilder(new File(scriptsDir, scriptName).getCanonicalPath());
            File stdoutLog = getStdoutFile();
            Map<String, String> env = pb.environment();
            pb.directory(scriptsDir);
            pb.redirectErrorStream(true);

            // Don't redirect to file - read directly so that we can notify UI
            //pb.redirectOutput(ProcessBuilder.Redirect.appendTo(stdoutLog));
            OutputStream processStdoutFile = Files.newOutputStream(stdoutLog.toPath());

            Process p = pb.start();
            // STDERR was redirected to STDOUT
            InputStream processStdoutAndStdErr = p.getInputStream();
            try {
                byte[] buffer = new byte[1024];
                while (p.isAlive())
                {
                    int nRead = processStdoutAndStdErr.read(buffer);
                    log.debug("Read " + nRead + " bytes from process stdout/stderr");

                    processStdoutFile.write(buffer, 0, nRead);
                }

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
