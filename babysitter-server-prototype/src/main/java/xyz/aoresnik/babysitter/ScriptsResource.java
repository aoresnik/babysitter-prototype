package xyz.aoresnik.babysitter;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.script.ScriptExecution;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Path("/api/v1/scripts")
public class ScriptsResource {

    @Inject
    Logger log;

    @Inject
    Vertx vertx;

    @Inject
    ScriptRunSessions scriptRunSessions;

    WorkerExecutor executor;

    public static final java.nio.file.Path SCRIPTS_DIR = java.nio.file.Path.of(
            // PROBLEM: quarkus runs the program in build/classes/java/main, this is simple workaround
            "..",
            "..",
            "..",
            "..",
            "babysitter",
            "scripts"
    );

    @PostConstruct
    void init() {
        this.executor = vertx.createSharedWorkerExecutor("my-worker", 10);
    }

    void runSync() {
        executor.<String>executeBlocking(promise -> {
            // TODO: run script in this thread
            // TODO: show that it's waiting for free thread if no thread is free
            log.info("Thread: " + Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            promise.complete("Async exec test done");
        }, asyncResult -> {
            // TODO: notify the websockets sessions listening to this result
            log.info(String.format("Result of async operation %s", asyncResult.result())); // Done
        });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getScripts() {
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

    /**
     * Returns the script session ID
     * TODO: not yet implemented, stub
     * @param scriptName
     * @return
     */
    @Path("/{scriptName}/run-async")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String runAsync(@PathParam("scriptName") String scriptName) {
        log.info(String.format("Running script %s async", scriptName));
        // TODO: just trigger here, return immediately
        ScriptExecution scriptExecution = new ScriptExecution(scriptName);
        scriptExecution.start();
        scriptExecution.waitFor();
        log.warn("TODO: actually run script in executor");

        // Explicitly wrap as JSON string (I don't know yet why it's not done automatically)
        ScriptRunSessions.ScriptRunSession scriptRunSession = scriptRunSessions.createForActiveExecution(scriptName, scriptExecution);
        return "\"" + scriptRunSession.getScriptExecution().getSessionId() + "\"";
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

        runSync();

        // TODO: return the lines as they are printed from ScriptRunSession
        return scriptExecution.getResult();
    }

}
