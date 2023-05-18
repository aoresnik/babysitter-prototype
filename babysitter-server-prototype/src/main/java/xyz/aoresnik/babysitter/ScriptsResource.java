package xyz.aoresnik.babysitter;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.ScriptData;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.entity.ScriptSource;
import xyz.aoresnik.babysitter.script.AbstractScriptType;
import xyz.aoresnik.babysitter.script.ActiveScriptExecutions;
import xyz.aoresnik.babysitter.script.AbstractScriptExecution;
import xyz.aoresnik.babysitter.script.ScriptTypes;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

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

    @Inject
    ScriptExecutionUpdateService scriptExecutionUpdateService;

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

    private void runAsyncInExecutor(AbstractScriptExecution scriptExecution) {
        executor.<String>executeBlocking(promise -> {
            // TODO: show that it's waiting for free thread if no thread is free
            log.info(String.format("Running script execution ID: %s in thread: %s", scriptExecution.getScriptExecutionID(), Thread.currentThread()));
            try {
                scriptExecution.start();
                scriptExecution.waitFor();
            } finally {
                activeScriptExecutions.removeScriptExecution(scriptExecution.getScriptExecutionID());
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
            AbstractScriptType scriptType = ScriptTypes.forScriptSource(scriptSource);

            List<String> scriptIds = scriptType.getScripts();
            log.debug("The source enumerated scripts: " + scriptIds);

            scriptIds.forEach(filename -> {
                    ScriptData scriptData = new ScriptData();
                    scriptData.setScriptSourceId(scriptSource.getId());
                    scriptData.setScriptSourceName(scriptSource.getName());
                    scriptData.setScriptId(filename);
                    result.add(scriptData);
                });
        }

        // This is only to check that status update get to the database
        // TODO: use this to return data to the client
        em.createQuery("select se from ScriptExecution se", ScriptExecution.class).getResultList().forEach(scriptExecution -> {
            log.debug("Found script execution: " + scriptExecution);
        });

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
    @Transactional
    public String runAsync(@PathParam("scriptSourceId") Long scriptSourceId, @PathParam("scriptName") String scriptName) {
        ScriptSource scriptSource = em.find(ScriptSource.class, scriptSourceId);

        log.info(String.format("Running script %s from script source ID=%d, NAME=%s async", scriptName, scriptSource.getId(), scriptSource.getName()));

        AbstractScriptType scriptType = ScriptTypes.forScriptSource(scriptSource);

        ScriptExecution scriptExecution = new ScriptExecution();
        scriptExecution.setScriptSource(scriptSource);
        scriptExecution.setScriptId(scriptName);
        em.persist(scriptExecution);

        log.debug("Started execution as SCRIPT_EXECUTION.ID=%d".formatted(scriptExecution.getId()));

        // Just trigger here, return immediately
        AbstractScriptExecution scriptExecutionRunner = scriptType.createScriptExecution(scriptName, Long.toString(scriptExecution.getId()));
        scriptExecutionRunner.updateEntity(scriptExecution);
        scriptExecutionRunner.addStatusChangeListener(scriptExecutionRunner1 -> {
            log.debug(String.format("Script execution ID=%s status changed - updating in DB", scriptExecutionRunner1.getScriptExecutionID()));
            scriptExecutionUpdateService.updateScriptExecution(scriptExecutionRunner1);
        });
        ScriptRunSessions.ScriptRunSession scriptRunSession = scriptRunSessions.createForActiveExecution(scriptName, scriptExecutionRunner);

        runAsyncInExecutor(scriptExecutionRunner);

        activeScriptExecutions.addScriptExecution(scriptExecutionRunner);

        // Explicitly wrap as JSON string (I don't know yet why it's not done automatically)
        return "\"" + scriptRunSession.getScriptExecution().getScriptExecutionID() + "\"";
    }

}
