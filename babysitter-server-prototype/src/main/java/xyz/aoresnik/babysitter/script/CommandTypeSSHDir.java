package xyz.aoresnik.babysitter.script;

import com.jcraft.jsch.*;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.CommandInputData;
import xyz.aoresnik.babysitter.entity.CommandExecution;
import xyz.aoresnik.babysitter.entity.CommandSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class CommandTypeSSHDir extends AbstractCommandType {

    private static final Logger log = Logger.getLogger(CommandTypeSSHDir.class);

    public CommandTypeSSHDir(CommandSource commandSource) {
        super(commandSource);
    }

    @Override
    public List<String> getScripts() {
        log.info("SSH script source script enumeration for " + getScriptSource());

        if (getScriptSource().getScriptSourceSSHDir().getSshConfig() == null)
        {
            log.warn("SSH Config is null for SCRIPT_SOURCE.ID=" + getScriptSource().getId() + " - ssh commands will not be available");
            return new ArrayList<>();
        }

        // Based on sample code at http://www.jcraft.com/jsch/examples/OpenSSHConfig.java.html

        String command1="ls " + getScriptSource().getScriptSourceSSHDir().getDirname();
        try{
            Session session = createSSHSession();
            session.connect();
            log.debug("SSH session: Connected");

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            StringBuilder output = new StringBuilder();
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    String s = new String(tmp, 0, i);
                    output.append(s);
                    log.debug("SSH session: output> "  + s);
                }
                if(channel.isClosed()){
                    log.debug("SSH session: exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(300);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            log.debug("SSH session: DONE");

            if (channel.getExitStatus() == 0) {
                log.info("Returning a list of scripts");
                return Arrays.asList(output.toString().split("\n"));
            } else {
                log.info("Error getting list of scripts - exit code " + channel.getExitStatus());
                return new ArrayList<>();
            }
        }catch(Exception e){
            log.error("Error while trying to run commands over SSH to get a list of script", e);
        }

        return new ArrayList<>();
    }

    private Session createSSHSession() throws JSchException {
        byte[] sshConfig = getScriptSource().getScriptSourceSSHDir().getSshConfig();
        if (sshConfig == null) {
            throw new RuntimeException("SSH Config is null for SCRIPT_SOURCE.ID=" + getScriptSource().getId());
        }
        OpenSSHConfig parsedOpenSSHConfig;
        try {
            parsedOpenSSHConfig = OpenSSHConfig.parse(new String(sshConfig, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse OpenSSH Config for SCRIPT_SOURCE.ID=" + getScriptSource().getId(), e);
        }

        // Vagrant creates config with "default" for the test VM
        ConfigRepository.Config defaultConfig = parsedOpenSSHConfig.getConfig("default");

        JSch jsch = new JSch();
        jsch.setConfigRepository(parsedOpenSSHConfig);
        Session session=jsch.getSession("default");
        session.setConfig("StrictHostKeyChecking", "no");
        return session;
    }

    @Override
    public AbstractCommandRunner createScriptExecution(String scriptName, String scriptExecutionID) {
        return new SSHDirCommandTypeRunner(getScriptSource(), scriptName, scriptExecutionID);
    }

    @Override
    public AbstractCommandRunner forInactiveScriptExecution(CommandExecution commandExecution) {
        SSHDirCommandTypeRunner result = new SSHDirCommandTypeRunner(getScriptSource(), commandExecution.getCommand().getScript(), Long.toString(commandExecution.getId()));
        result.initFromScriptExecutionEntity(commandExecution);
        return result;
    }

    class SSHDirCommandTypeRunner extends AbstractCommandRunner {

        private OutputStream processStdin;

        public SSHDirCommandTypeRunner(CommandSource commandSource, String scriptName, String scriptExecutionID) {
            super(commandSource, scriptName, scriptExecutionID);
        }

        @Override
        public void run() {
            String command1=getScriptSource().getScriptSourceSSHDir().getDirname()+"/"+getScriptName();

            try (OutputStream processStdoutLog = Files.newOutputStream(getStdoutFile().toPath())) {
                setStartTime(new Date());

                Session session = createSSHSession();
                session.connect();
                log.debug("SSH session: Connected");

                ChannelExec channel= (ChannelExec) session.openChannel("exec");
                channel.setCommand(command1);
                channel.setInputStream(null);
                channel.setPty(true);

                InputStream in=channel.getInputStream();
                processStdin = channel.getOutputStream();
                channel.connect();
                byte[] tmp=new byte[1024];

                setScriptRun(true);
                saveStatusChange();
                notifyConsoleChangeListeners("", null);

                // Based on sample code at http://www.jcraft.com/jsch/examples/OpenSSHConfig.java.html

                while (true) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    processStdoutLog.write(tmp, 0, i);
                    processStdoutLog.flush();
                    notifyConsoleChangeListeners(getErrorText(), Arrays.copyOf(tmp, i));
                    String s = new String(tmp, 0, i);
                    log.debug("SSH session: output> " + s);
                }
                channel.disconnect();
                session.disconnect();
                log.debug("SSH session: DONE");

                setScriptCompleted(true);
                setExitCode(channel.getExitStatus());

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
