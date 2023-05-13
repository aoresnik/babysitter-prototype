package xyz.aoresnik.babysitter;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.ScriptData;
import xyz.aoresnik.babysitter.entity.ScriptSource;
import xyz.aoresnik.babysitter.script.ActiveScriptExecutions;
import xyz.aoresnik.babysitter.script.ScriptExecution;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    @Inject
    ActiveScriptExecutions activeScriptExecutions;

    WorkerExecutor executor;

    @Inject
    EntityManager em;

    @PostConstruct
    void init() {
        this.executor = vertx.createSharedWorkerExecutor("scripts-execution-worker", 10);
    }

    /**
     * Test method for tinkering with running in executor.
     */
    private void testRunInExecutor() {
        executor.<String>executeBlocking(promise -> {
            log.info("Thread: " + Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            promise.complete("Async exec test done");
        }, asyncResult -> {
            log.info(String.format("Result of async operation %s", asyncResult.result())); // Done
        });
    }

    private void runAsyncInExecutor(ScriptExecution scriptExecution) {
        executor.<String>executeBlocking(promise -> {
            // TODO: show that it's waiting for free thread if no thread is free
            log.info(String.format("Running script execution ID: %s in thread: %s", scriptExecution.getSessionId(), Thread.currentThread()));
            try {
                scriptExecution.start();
                scriptExecution.waitFor();
            } finally {
                activeScriptExecutions.removeScriptExecution(scriptExecution.getSessionId());
            }
            promise.complete("Script execution done");
        }, asyncResult -> {
            // TODO: notify the websockets sessions listening to this result
            // TODO: store the error result
            log.info(String.format("Result of async script run %s", asyncResult.result())); // Done
        });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScriptData> getScripts() {
        log.info("Reading the list of scripts");
        List<ScriptSource> scriptSources = em.createQuery("select ss from ScriptSource ss", ScriptSource.class).getResultList();

        List<ScriptData> result = new ArrayList<>();

        for (ScriptSource scriptSource : scriptSources) {
            log.info(String.format("Detecting scripts in source: %s", scriptSource));

            if (scriptSource.getScriptSourceServerDir() != null) {
                String currentPath = null;
                try {
                    currentPath = new File(".").getCanonicalPath();
                    log.info("Current dir: " + currentPath + " NOTE: Quarkus runs in classes/java/main, so the scripts are in ../../..");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String dirName = scriptSource.getScriptSourceServerDir().getDirname();
                log.debug(String.format("Reading scripts from dir: %s", dirName));
                File scriptsDir = new File(currentPath, dirName);
                File[] files = scriptsDir.listFiles(file -> file.getName().endsWith(".sh"));
                if (files == null) {
                    throw new RuntimeException("Invalid scripts dir in configuration: " + scriptsDir);
                }
                List<String> filenamesList = Arrays.stream(files).map(File::getName).collect(Collectors.toList());

                filenamesList.forEach(filename -> {
                    ScriptData scriptData = new ScriptData();
                    scriptData.setScriptSourceId(scriptSource.getId());
                    scriptData.setScriptId(filename);
                    result.add(scriptData);
                });
            }
        }

        return result;
    }

    /**
     * Returns the script session ID
     * @param scriptName
     * @return
     */
    @Path("/{scriptSourceId}/{scriptName}/run-async")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String runAsync(@PathParam("scriptSourceId") Long scriptSourceId, @PathParam("scriptName") String scriptName) {
        ScriptSource scriptSource = em.find(ScriptSource.class, scriptSourceId);

        log.info(String.format("Running script %s from script source ID=%d, NAME=%s async", scriptName, scriptSource.getId(), scriptSource.getName()));
        // Just trigger here, return immediately
        ScriptExecution scriptExecution = null;
        try {
            scriptExecution = new ScriptExecution(scriptSource, scriptName);
        } catch (IOException e) {
            throw new RuntimeException("Internal error while attempting to run script", e);
        }
        runAsyncInExecutor(scriptExecution);

        activeScriptExecutions.addScriptExecution(scriptExecution);

        // Explicitly wrap as JSON string (I don't know yet why it's not done automatically)
        ScriptRunSessions.ScriptRunSession scriptRunSession = scriptRunSessions.createForActiveExecution(scriptName, scriptExecution);
        return "\"" + scriptRunSession.getScriptExecution().getSessionId() + "\"";
    }

//    @Path("/{scriptName}/run")
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<String> run(@PathParam("scriptName") String scriptName) {
//        log.info(String.format("Running script %s", scriptName));
//        ScriptExecution scriptExecution = null;
//        try {
//            scriptExecution = new ScriptExecution(scriptSource, scriptName);
//        } catch (IOException e) {
//            throw new RuntimeException("Internal error while attempting to run script", e);
//        }
//        // TODO: just trigger here, return immediately
//        scriptExecution.start();
//        scriptExecution.waitFor();
//
//        testRunInExecutor();
//
//        // TODO: return the lines as they are printed from ScriptRunSession
//        return Arrays.asList(new String(scriptExecution.getResult(), StandardCharsets.UTF_8).split("\n"));
//    }

}
