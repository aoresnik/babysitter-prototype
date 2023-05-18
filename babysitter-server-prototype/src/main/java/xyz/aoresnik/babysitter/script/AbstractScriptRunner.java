package xyz.aoresnik.babysitter.script;

import lombok.Getter;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.ScriptExecutionData;
import xyz.aoresnik.babysitter.data.ScriptExecutionInitialStateData;
import xyz.aoresnik.babysitter.data.ScriptExecutionUpdateData;
import xyz.aoresnik.babysitter.data.ScriptInputData;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

abstract public class AbstractScriptRunner {

    Logger log = Logger.getLogger(AbstractScriptRunner.class);

    @Getter
    private final String scriptExecutionID;

    private final ScriptSource scriptSource;
    @Getter
    private final String scriptName;

    @Getter
    private String errorText;

    private boolean scriptRun = false;

    private boolean scriptCompleted = false;

    @Getter
    private Integer exitCode;

    private Set<Consumer<ScriptExecutionData>> listeners = new HashSet<>();

    private Set<Consumer<AbstractScriptRunner>> statusChangeListeners = new HashSet<>();

    public AbstractScriptRunner(ScriptSource scriptSource, String scriptName, String executionId) {
        this.scriptSource = scriptSource;
        this.scriptName = scriptName;
        this.scriptExecutionID = executionId;
        try {
            getStdoutFile().createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create STDOUT file", e);
        }
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

    /**
     * Must run the script in current thread (it's called from appropriate workers).
     * Output (both STDOUT and STDERR) must be sent to listeners and write to the file returned by {@link #getStdoutFile()}
     * Input is sent in another thread to {@link #sendInput(ScriptInputData)}
     */
    abstract public void run();

    protected void notifyConsoleChangeListeners(String errorText1, byte[] incrementalConsoleData) {
        ScriptExecutionUpdateData updateData = new ScriptExecutionUpdateData();
        updateData.setScriptRun(scriptRun);
        updateData.setScriptCompleted(scriptCompleted);
        updateData.setExitCode(null);
        updateData.setErrorText(errorText1);
        updateData.setIncrementalConsoleData(incrementalConsoleData);

        synchronized (listeners) {
            log.debug(String.format("Notifying %d listeners", listeners.size()));
            for (Consumer<ScriptExecutionData> listener : listeners) {
                listener.accept(updateData);
            }
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

    protected File getStdoutFile() {
        return new File(System.getProperty("java.io.tmpdir"), "stdout-" + scriptExecutionID + ".log");
    }

    abstract public void sendInput(ScriptInputData message);

    public String getErrorText() {
        return errorText;
    }

    protected void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public boolean isScriptRun() {
        return scriptRun;
    }

    protected void setScriptRun(boolean scriptRun) {
        this.scriptRun = scriptRun;
    }

    public boolean isScriptCompleted() {
        return scriptCompleted;
    }

    protected void setScriptCompleted(boolean scriptCompleted) {
        this.scriptCompleted = scriptCompleted;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    protected void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public void saveStatusChange() {
        statusChangeListeners.forEach(listener -> listener.accept(this));
    }

    public void updateEntity(ScriptExecution scriptExecution) {
        scriptExecution.setScriptCompleted(scriptCompleted);
        scriptExecution.setScriptRun(scriptRun);
        scriptExecution.setErrorText(errorText);
        scriptExecution.setExitCode(exitCode);
    }

    public void addStatusChangeListener(Consumer<AbstractScriptRunner> statusChangeListener) {
        this.statusChangeListeners.add(statusChangeListener);
    }
}
