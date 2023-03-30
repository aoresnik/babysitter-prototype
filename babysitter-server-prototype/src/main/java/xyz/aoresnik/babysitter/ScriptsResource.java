package xyz.aoresnik.babysitter;

import io.quarkus.logging.Log;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 */
@Path("/api/v1/scripts")
public class ScriptsResource {

    @Inject
    Logger log;

    public static final java.nio.file.Path SCRIPTS_DIR = java.nio.file.Path.of(
            // PROBLEM: quarkus runs the program in build/classes/java/main, this is simple workaround
            "..",
            "..",
            "..",
            "..",
            "babysitter",
            "scripts"
    );

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        String currentPath = null;
        try {
            currentPath = new File(".").getCanonicalPath();
            log.info("Current dir: " + currentPath + " NOTE: Quarkus runs in classes/java/main that the scripts are in ../../..");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File scriptsDir = SCRIPTS_DIR.toFile();
        File[] files = scriptsDir.listFiles(file -> file.getName().endsWith(".sh"));
        return Arrays.stream(files).map(File::getName).collect(Collectors.joining("\n"));
    }
}
