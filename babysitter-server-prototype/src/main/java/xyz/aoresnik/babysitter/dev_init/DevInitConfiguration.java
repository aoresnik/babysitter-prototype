package xyz.aoresnik.babysitter.dev_init;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.ScriptSourceSSHDir;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DevInitConfiguration {
    public static final String DEV_DATABASE_INIT_SQL = "/dev-database-init.sql";
    @Inject
    Logger log;

    @Inject
    AgroalDataSource defaultDataSource;

    @Inject
    EntityManager em;

    void onStart(@Observes StartupEvent ev) {
        log.info("The application is starting: initializing the dev environment database");

        String contents;
        try (InputStream inputStream = getClass().getResourceAsStream(DEV_DATABASE_INIT_SQL);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            contents = reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read resource data from system resource " + DEV_DATABASE_INIT_SQL, e);
        }

        try (Connection connection = defaultDataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                log.debug("Executing initialization SQL");
                boolean execute = statement.execute(contents);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to open database", e);
        }

        // Read SSH config files for connecting to vagrant test VMs that have been prepared by scripts called from the Vagrantfile
        List<ScriptSourceSSHDir> selectSssdFromScriptSourceSSHDirSssd = em.createQuery("select sssd from ScriptSourceSSHDir sssd").getResultList();
        for (ScriptSourceSSHDir sssd : selectSssdFromScriptSourceSSHDirSssd) {
            log.info("Found ScriptSourceSSHDir: " + sssd);
            String name = sssd.getScriptSource().getName();
            String sshConfigNameBase = name.split("\\ ")[0];
            log.debug(String.format("For name %s the SSH config resource file name base is %s", name, sshConfigNameBase));
            String sshConfigFileName = sshConfigNameBase + ".dev.ssh-config";

            try (InputStream inputStream = getClass().getResourceAsStream("/" + sshConfigFileName)) {
                if (inputStream != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        String sshConfigFileContents;
                        sshConfigFileContents = reader.lines()
                                .collect(Collectors.joining(System.lineSeparator()));
                        log.debug("Read SSH config resource from " + sshConfigFileName);
                        sssd.setSshConfig(sshConfigFileContents.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    log.warn("Unable to find SSH config for connection to dev test-target-vm " + sshConfigFileName + " - scripts execution via SSH will not be available");
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to read config resource " + sshConfigFileName,e);
            }
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
    }

}
