package xyz.aoresnik.babysitter.script;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.AbstractScriptExecutionRTData;
import xyz.aoresnik.babysitter.data.ScriptExecutionInitialStateRTData;
import xyz.aoresnik.babysitter.data.ScriptExecutionUpdateRTData;
import xyz.aoresnik.babysitter.data.ScriptInputData;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import javax.persistence.Column;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Date;
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
    @Setter(AccessLevel.PROTECTED)
    private String errorText;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean scriptRun = false;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean scriptCompleted = false;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Integer exitCode;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Date startTime;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Date endTime;

    private Set<Consumer<AbstractScriptExecutionRTData>> listeners = new HashSet<>();

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
    public ScriptExecutionInitialStateRTData registerConsoleChangeListener(Consumer<AbstractScriptExecutionRTData> listener) {
        synchronized (listeners) {
            listeners.add(listener);
            return getScriptExecutionInitialStateData();
        }
    }

    /**
     * Note: when registering the listener with {@link #registerConsoleChangeListener(Consumer)}, use the value
     * returned by that method as the initial state - because it's guaranteed that updates will be incremental with
     * regard to that initial state.
     * @return
     */
    public ScriptExecutionInitialStateRTData getScriptExecutionInitialStateData() {
        byte[] resultText = getResult();
        ScriptExecutionInitialStateRTData initialStateData = new ScriptExecutionInitialStateRTData();

        initialStateData.setScriptRun(scriptRun);
        initialStateData.setScriptCompleted(scriptCompleted);
        initialStateData.setExitCode(exitCode);
        initialStateData.setErrorText(getErrorText());
        initialStateData.setInitialConsoleData(resultText);

        return initialStateData;
    }

    public void removeConsoleChangeListener(Consumer<AbstractScriptExecutionRTData> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Must run the script in current thread (it's called from appropriate workers).
     * Output (both STDOUT and STDERR) must be sent to listeners and write to the file returned by {@link #getStdoutFile()}
     * Input is sent in another thread to {@link #sendInput(ScriptInputData)}
     * Must set:
     * <ul>
     * <li>{@link #scriptRun} to true when script starts, and set {@link #startTime}</<li>
     * <li>{@link #scriptCompleted} to true when script completes, and set {@link #endTime}</li>
     * </ul>
     * After each update, it must call {@link #notifyConsoleChangeListeners(String, byte[])} (if console changes) or
     * {@link #saveStatusChange()} if only status fields changes.
     */
    abstract public void run();

    protected void notifyConsoleChangeListeners(String errorText1, byte[] incrementalConsoleData) {
        ScriptExecutionUpdateRTData updateData = new ScriptExecutionUpdateRTData();
        updateData.setScriptRun(scriptRun);
        updateData.setScriptCompleted(scriptCompleted);
        updateData.setExitCode(exitCode);
        updateData.setErrorText(errorText1);
        updateData.setIncrementalConsoleData(incrementalConsoleData);

        synchronized (listeners) {
            log.debug(String.format("Notifying %d listeners", listeners.size()));
            for (Consumer<AbstractScriptExecutionRTData> listener : listeners) {
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

    public void saveStatusChange() {
        statusChangeListeners.forEach(listener -> listener.accept(this));
    }

    public void updateEntity(ScriptExecution scriptExecution) {
        scriptExecution.setScriptCompleted(scriptCompleted);
        scriptExecution.setScriptRun(scriptRun);
        scriptExecution.setErrorText(errorText);
        scriptExecution.setExitCode(exitCode);
        scriptExecution.setStartTime(startTime != null ? new Timestamp(startTime.getTime()) : null);
        scriptExecution.setEndTime(endTime != null ? new Timestamp(endTime.getTime()) : null);
    }

    public void addStatusChangeListener(Consumer<AbstractScriptRunner> statusChangeListener) {
        this.statusChangeListeners.add(statusChangeListener);
    }
}
