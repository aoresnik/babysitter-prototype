package xyz.aoresnik.babysitter.script;

import com.jcraft.jsch.*;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.ScriptSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptTypeSSHDir extends AbstractScriptType {

    private static final Logger log = Logger.getLogger(ScriptTypeServerDir.class);

    public ScriptTypeSSHDir(ScriptSource scriptSource) {
        super(scriptSource);
    }

    @Override
    public List<String> getScripts() {
        log.error("SSH script source script enumeration for " + getScriptSource());

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
            String output = "";
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    String s = new String(tmp, 0, i);
                    output += s;
                    log.debug("SSH session: output> "  + s);
                }
                if(channel.isClosed()){
                    log.debug("SSH session: exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            log.debug("SSH session: DONE");

            if (channel.getExitStatus() == 0) {
                log.info("Returning a list of scripts");
                return Arrays.asList(output.split("\n"));
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
    public AbstractScriptExecution createScriptExecution(String scriptName) {
        throw new RuntimeException("SSH script source script execution not yet implemented");
    }
}
