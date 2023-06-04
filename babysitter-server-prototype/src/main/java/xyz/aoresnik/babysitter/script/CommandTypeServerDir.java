package xyz.aoresnik.babysitter.script;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.CommandInputData;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class CommandTypeServerDir extends AbstractCommandType {

    private static final Logger log = Logger.getLogger(CommandTypeServerDir.class);

    public CommandTypeServerDir(ScriptSource scriptSource) {
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
    public AbstractCommandRunner createScriptExecution(String scriptName, String scriptExecutionID)  {
        return new ServerDirCommandTypeRunner(getScriptSource(), scriptName, scriptExecutionID);
    }

    @Override
    public AbstractCommandRunner forInactiveScriptExecution(ScriptExecution scriptExecution) {
        ServerDirCommandTypeRunner result = new ServerDirCommandTypeRunner(getScriptSource(), scriptExecution.getScriptId(), Long.toString(scriptExecution.getId()));
        result.initFromScriptExecutionEntity(scriptExecution);
        return result;
    }

    class ServerDirCommandTypeRunner extends AbstractCommandRunner {

        private OutputStream processStdin;

        public ServerDirCommandTypeRunner(ScriptSource scriptSource, String scriptName, String scriptExecutionID) {
            super(scriptSource, scriptName, scriptExecutionID);
        }

        @Override
        public void run() {
            if (isScriptRun()) {
                throw new IllegalStateException("Script was already run");
            }
            File stdoutLog = getStdoutFile();
            try (OutputStream processStdoutLog = Files.newOutputStream(stdoutLog.toPath())) {
                setStartTime(new Date());

                File scriptsDir = new File(getScriptSource().getScriptSourceServerDir().getDirname());
                //ProcessBuilder pb = new ProcessBuilder(new File(scriptsDir, scriptName).getCanonicalPath());

                // PROBLEM with JPty - it doesn't show detailed errors, it returns "Exec_tty error:Unknown reason"
                // instead of "not executable", as vanilla ProcessBuilder does

                PtyProcessBuilder pb = new PtyProcessBuilder().setCommand(new String[] {new File(scriptsDir, getScriptName()).getCanonicalPath()});
                //Map<String, String> env = pb.environment();
                pb.setDirectory(scriptsDir.getCanonicalPath());
                pb.setRedirectErrorStream(true);

                Map<String, String> env = new HashMap<>();
                env.put("TERM", "xterm-256color");
                pb.setEnvironment(env);

                // Don't redirect to file - read directly so that we can notify UI
                //pb.redirectOutput(ProcessBuilder.Redirect.appendTo(stdoutLog));

                PtyProcess p = pb.start();

                setScriptRun(true);
                saveStatusChange();

                notifyConsoleChangeListeners("", null);

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

                            notifyConsoleChangeListeners(getErrorText(), Arrays.copyOf(buffer, nRead));
                        } else {
                            log.debug("Read EOF from process stdout/stderr - stopping");
                            break;
                        }
                    }

                    setScriptCompleted(true);
                    setExitCode(p.waitFor());

                } catch (InterruptedException e) {
                    log.info("Detected interrupt exception, stopping process");
                    p.destroy();
                }

            } catch (Exception e) {
                log.error("Error while trying to run commands over SSH to get a list of script", e);
                setErrorText(e.getMessage());
            } finally {
                setEndTime(new Date());
                saveStatusChange();
                notifyConsoleChangeListeners(getErrorText(), null);
            }
        }

        @Override
        public void sendInput(CommandInputData message) {
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
}
