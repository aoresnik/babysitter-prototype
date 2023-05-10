package xyz.aoresnik.babysitter.script;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import lombok.Getter;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.ScriptExecutionData;
import xyz.aoresnik.babysitter.data.ScriptExecutionInitialStateData;
import xyz.aoresnik.babysitter.data.ScriptExecutionUpdateData;
import xyz.aoresnik.babysitter.data.ScriptInputData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

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

    private boolean scriptRun = false;

    private boolean scriptCompleted = false;

    @Getter
    private Integer exitCode;

    OutputStream processStdin;

    private Set<Consumer<ScriptExecutionData>> listeners = new HashSet<>();

    public ScriptExecution(String scriptName) throws IOException {
        this.scriptName = scriptName;
        this.sessionId = UUID.randomUUID().toString();
        getStdoutFile().createNewFile();
    }

    /**
     * Registers the listener for execution status and console output updates and returns the initial state.
     * It's guaranteed that the initial state and updates will contain entire console output.
     * @param listener
     * @return
     */
    public ScriptExecutionInitialStateData registerListener(Consumer<ScriptExecutionData> listener) {
        synchronized (listeners) {
            listeners.add(listener);
            byte[] resultText = getResult();
            ScriptExecutionInitialStateData initialStateData = new ScriptExecutionInitialStateData();

            // TODO: return correct state
            initialStateData.setScriptRun(scriptRun);
            initialStateData.setScriptCompleted(scriptCompleted);
            initialStateData.setExitCode(exitCode);
            initialStateData.setErrorText(getErrorText());
            initialStateData.setInitialConsoleData(resultText);

            return initialStateData;
        }
    }

    public void removeListener(Consumer<ScriptExecutionData> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void start() {
        try {

            File scriptsDir = SCRIPTS_DIR.toFile();
            //ProcessBuilder pb = new ProcessBuilder(new File(scriptsDir, scriptName).getCanonicalPath());

            // PROBLEM with JPty - it doesn't show detailed errors, it returns "Exec_tty error:Unknown reason"
            // instead of "not executable", as vanilla ProcessBuilder does

            PtyProcessBuilder pb = new PtyProcessBuilder().setCommand(new String[] {new File(scriptsDir, scriptName).getCanonicalPath()});
            File stdoutLog = getStdoutFile();
            //Map<String, String> env = pb.environment();
            pb.setDirectory(scriptsDir.getCanonicalPath());
            pb.setRedirectErrorStream(true);

            // Don't redirect to file - read directly so that we can notify UI
            //pb.redirectOutput(ProcessBuilder.Redirect.appendTo(stdoutLog));
            OutputStream processStdoutLog = Files.newOutputStream(stdoutLog.toPath());

            PtyProcess p = pb.start();

            scriptRun = true;

            {
                ScriptExecutionUpdateData updateData = new ScriptExecutionUpdateData();
                updateData.setScriptRun(scriptRun);
                updateData.setScriptCompleted(scriptCompleted);
                updateData.setExitCode(null);
                updateData.setErrorText("");
                updateData.setIncrementalConsoleData(null);

                synchronized (listeners) {
                    for (Consumer<ScriptExecutionData> listener : listeners) {
                        listener.accept(updateData);
                    }
                }
            }

            // STDERR was redirected to STDOUT
            InputStream processStdoutAndStdErr = p.getInputStream();
            processStdin = p.getOutputStream();
            try {
                byte[] buffer = new byte[1024];
                while (true)
                {
                    int nRead = processStdoutAndStdErr.read(buffer);
                    if (nRead >= 0) {
                        log.debug("Read " + nRead + " bytes from process stdout/stderr");

                        processStdoutLog.write(buffer, 0, nRead);
                        processStdoutLog.flush();

                        ScriptExecutionUpdateData updateData = new ScriptExecutionUpdateData();
                        updateData.setScriptRun(scriptRun);
                        updateData.setScriptCompleted(scriptCompleted);
                        updateData.setExitCode(null);
                        updateData.setErrorText(getErrorText());
                        updateData.setIncrementalConsoleData(Arrays.copyOf(buffer, nRead));

                        synchronized (listeners) {
                            log.debug(String.format("Notifying %d listeners", listeners.size()));
                            for (Consumer<ScriptExecutionData> listener : listeners) {
                                listener.accept(updateData);
                            }
                        }
                    } else {
                        log.debug("Read EOF from process stdout/stderr - stopping");
                        break;
                    }
                }

                this.scriptCompleted = true;
                this.exitCode = p.waitFor();

                {
                    ScriptExecutionUpdateData updateData = new ScriptExecutionUpdateData();
                    updateData.setScriptRun(scriptRun);
                    updateData.setScriptCompleted(scriptCompleted);
                    updateData.setExitCode(getExitCode());
                    updateData.setErrorText(getErrorText());
                    updateData.setIncrementalConsoleData(null);

                    synchronized (listeners) {
                        for (Consumer<ScriptExecutionData> listener : listeners) {
                            listener.accept(updateData);
                        }
                    }
                }

            } catch (InterruptedException e) {
                log.info("Detected interrupt exception, stopping process");
                p.destroy();
            }
        } catch (IOException e) {
            log.error("Error running script", e);
            errorText = e.getMessage();
        }
    }

    public byte[] getResult() {
        byte[] result = null;
        try {
            result = Files.readAllBytes(getStdoutFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read result from file", e);
        }
        log.debug("Result: " + result.length + " bytes");
        return result;
    }

    private File getStdoutFile() {
        return new File(System.getProperty("java.io.tmpdir"), "stdout-" + sessionId + ".log");
    }

    public void waitFor() {
        // For now, entire execution is in start
    }

    public void sendInput(ScriptInputData message) {
        if (processStdin != null) {
            try {
                processStdin.write(Base64.getDecoder().decode(message.getInputData()));
                processStdin.flush();
            } catch (IOException e) {
                throw new RuntimeException("Unable to send input to process", e);
            }
        } else {
            log.info("Process STDIN is not yet available, ignoring input");
        }
    }
}
