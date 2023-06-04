package xyz.aoresnik.babysitter;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.CommandData;
import xyz.aoresnik.babysitter.data.CommandLastUsedData;
import xyz.aoresnik.babysitter.data.CommandMostUsedData;
import xyz.aoresnik.babysitter.entity.Command;
import xyz.aoresnik.babysitter.entity.CommandExecution;
import xyz.aoresnik.babysitter.entity.CommandSource;
import xyz.aoresnik.babysitter.script.AbstractCommandRunner;
import xyz.aoresnik.babysitter.script.AbstractCommandType;
import xyz.aoresnik.babysitter.script.ActiveCommandRunners;
import xyz.aoresnik.babysitter.script.CommandTypes;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO: At the time of this writing, scripts are identified by name, not ID - hard to add endpoionts under the /{scriptSourceId} paths
 * TODO: also change the format of /api/v1/scripts/{scriptSourceId}/executions/{scriptExecutionId} to /api/v1/command-sources/{commandSourceId}/commands/{commandId}
 */
@Path("/api/v1/commands")
public class CommandsResource {

    @Inject
    Logger log;

    @Inject
    Vertx vertx;

    @Inject
    CommandRunSessions commandRunSessions;

    @Inject
    ActiveCommandRunners activeScriptRunners;

    @Inject
    CommandExecutionService commandExecutionService;

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

    private void runAsyncInExecutor(AbstractCommandRunner scriptExecution) {
        executor.<String>executeBlocking(promise -> {
            // TODO: show that it's waiting for free thread if no thread is free
            log.info(String.format("Running script execution ID: %s in thread: %s", scriptExecution.getScriptExecutionID(), Thread.currentThread()));
            try {
                scriptExecution.run();
            } finally {
                activeScriptRunners.removeScriptExecution(scriptExecution.getScriptExecutionID());
            }
            promise.complete("Script execution done");
        }, asyncResult -> {
            // TODO: notify the websockets sessions listening to this result
            // TODO: store the error result
            log.info(String.format("Result of async script run %s", asyncResult.result())); // Done
        });
    }

    /**
     * <p>Returns the list of scripts.</p>
     *
     * <p>Note that scripts are currently directly in the database, but only their directories are. So the scripts are
     * autodiscovered at this time</p>
     *
     * <p>TODO: consider keeping them in the database and autodiscover periodically or on demand</p>
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CommandData> getScripts() {
        log.info("Reading the list of commands from database");
        List<Command> commands = em.createQuery("select c from Command c", Command.class).getResultList();

        List<CommandData> result = new ArrayList<>();

        commands.forEach(command -> {
                CommandData commandData = new CommandData();
                commandData.setCommandSourceId(command.getCommandSource().getId());
                commandData.setCommandSourceName(command.getCommandSource().getName());
                commandData.setCommandId(command.getScript());
                result.add(commandData);
            });

        return result;
    }

    @Path("/most-used")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CommandMostUsedData> getMostUsedCommands() {
        log.info("Reading the list of most used commands");
        List<Object[]> scripts = em.createQuery("select se.command.commandSource, se.command.script, COUNT(*) from CommandExecution se GROUP BY se.command ORDER BY COUNT(*) DESC")
                .setMaxResults(10)
                .getResultList();

        List<CommandMostUsedData> result = new ArrayList<>();

        for (Object[] resultItem : scripts) {
            CommandSource commandSource = (CommandSource) resultItem[0];
            String scriptId = (String) resultItem[1];
            Long count = (Long) resultItem[2];
            log.info(String.format("Result: source %s, script %s, used %d times", commandSource.getName(), scriptId, count));

            CommandData commandData = new CommandData();
            commandData.setCommandSourceId(commandSource.getId());
            commandData.setCommandSourceName(commandSource.getName());
            commandData.setCommandId(scriptId);

            CommandMostUsedData commandMostUsedData = new CommandMostUsedData();
            commandMostUsedData.setCommandData(commandData);
            commandMostUsedData.setUsageCount(count);
            result.add(commandMostUsedData);
        }

        return result;
    }

    @Path("/last-used")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CommandLastUsedData> getLastUsedCommands() {
        log.info("Reading the list of most used commands");
        List<Object[]> scripts = em.createQuery("select se.command.commandSource, se.command.script, MAX(se.startTime) from CommandExecution se GROUP BY se.command ORDER BY MAX(se.startTime) DESC")
                .setMaxResults(10)
                .getResultList();

        List<CommandLastUsedData> result = new ArrayList<>();

        for (Object[] resultItem : scripts) {
            CommandSource commandSource = (CommandSource) resultItem[0];
            String scriptId = (String) resultItem[1];
            Timestamp lastUsage = (Timestamp) resultItem[2];
            log.info(String.format("Result: source %s, script %s, last used %s", commandSource.getName(), scriptId, lastUsage));

            CommandData commandData = new CommandData();
            commandData.setCommandSourceId(commandSource.getId());
            commandData.setCommandSourceName(commandSource.getName());
            commandData.setCommandId(scriptId);

            CommandLastUsedData commandLastUsedData = new CommandLastUsedData();
            commandLastUsedData.setCommandData(commandData);
            commandLastUsedData.setLastUsed(new Date(lastUsage.getTime()));
            result.add(commandLastUsedData);
        }

        return result;
    }

    /**
     * Returns the script session ID
     * @param scriptName
     * @return
     */
    @Path("/sources/{scriptSourceId}/{scriptName}/run-async")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public String runAsync(@PathParam("scriptSourceId") Long scriptSourceId, @PathParam("scriptName") String scriptName) {
        Command command = em.createQuery("select c from Command c where c.commandSource.id = :id AND c.script = :scriptName", Command.class)
                .setParameter("id", scriptSourceId)
                .setParameter("scriptName", scriptName)
                .getSingleResult();

        CommandSource commandSource = command.getCommandSource();

        log.info(String.format("Running script %s from script source ID=%d, NAME=%s async", scriptName, commandSource.getId(), commandSource.getName()));

        AbstractCommandType scriptType = CommandTypes.newForScriptSource(commandSource);

        CommandExecution commandExecution = new CommandExecution();
        commandExecution.setCommand(command);
        em.persist(commandExecution);

        log.debug("Started execution as SCRIPT_EXECUTION.ID=%d".formatted(commandExecution.getId()));

        // Just trigger here, return immediately
        AbstractCommandRunner scriptExecutionRunner = scriptType.createScriptExecution(scriptName, Long.toString(commandExecution.getId()));
        scriptExecutionRunner.updateEntity(commandExecution);
        scriptExecutionRunner.addStatusChangeListener(scriptExecutionRunner1 -> {
            log.debug(String.format("Script execution ID=%s status changed - updating in DB", scriptExecutionRunner1.getScriptExecutionID()));
            commandExecutionService.updateScriptExecution(scriptExecutionRunner1);
        });
        commandRunSessions.createForActiveExecution(scriptName, scriptExecutionRunner);

        runAsyncInExecutor(scriptExecutionRunner);

        // Explicitly wrap as JSON string (I don't know yet why it's not done automatically)
        return "\"" + scriptExecutionRunner.getScriptExecutionID() + "\"";
    }

}
