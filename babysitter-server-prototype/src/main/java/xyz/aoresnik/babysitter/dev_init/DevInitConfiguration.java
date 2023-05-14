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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        List<ScriptSourceSSHDir> selectSssdFromScriptSourceSSHDirSssd = em.createQuery("select sssd from ScriptSourceSSHDir sssd").getResultList();
        for (ScriptSourceSSHDir sssd : selectSssdFromScriptSourceSSHDirSssd) {
            log.info("Found ScriptSourceSSHDir: " + sssd);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
    }

}
