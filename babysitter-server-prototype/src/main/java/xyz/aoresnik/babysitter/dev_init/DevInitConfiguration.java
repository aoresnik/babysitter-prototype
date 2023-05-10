package xyz.aoresnik.babysitter.dev_init;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class DevInitConfiguration {
    @Inject
    Logger log;

    void onStart(@Observes StartupEvent ev) {
        log.info("The application is starting: initializing the dev environment database");
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
    }

}
