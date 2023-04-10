package xyz.aoresnik.babysitter;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.script.ScriptExecution;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> hello() {
        String currentPath = null;
        try {
            currentPath = new File(".").getCanonicalPath();
            log.info("Current dir: " + currentPath + " NOTE: Quarkus runs in classes/java/main that the scripts are in ../../..");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File scriptsDir = SCRIPTS_DIR.toFile();
        File[] files = scriptsDir.listFiles(file -> file.getName().endsWith(".sh"));
        List<String> filenamesList = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        return filenamesList;
    }

    @Path("/{scriptName}/run")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> run(@PathParam("scriptName") String scriptName) {
        log.info(String.format("Running script %s", scriptName));
        ScriptExecution scriptExecution = new ScriptExecution(scriptName);
        // TODO: just trigger here, return immediately
        scriptExecution.start();
        scriptExecution.waitFor();

        // TODO: return the lines as they are printed from ScriptRunSession
        return scriptExecution.getResult();
    }

}
