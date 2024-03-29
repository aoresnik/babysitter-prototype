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
 * TODO: At the time of this writing, scripts are identified by name, not ID - hard to add endpoints under the /{scriptSourceId} paths
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
            CommandData commandData = commandDataFromCommandEntity(command);
            result.add(commandData);
        });

        return result;
    }

    private static CommandData commandDataFromCommandEntity(Command command) {
        CommandData commandData = new CommandData();
        commandData.setCommandSourceId(command.getCommandSource().getId());
        commandData.setCommandSourceName(command.getCommandSource().getName());
        commandData.setCommandId(command.getId().toString());
        commandData.setCommandName(command.getName());
        return commandData;
    }

    @Path("/most-used")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CommandMostUsedData> getMostUsedCommands() {
        log.info("Reading the list of most used commands");
        List<Object[]> scripts = em.createQuery("select se.command, COUNT(*) from CommandExecution se GROUP BY se.command ORDER BY COUNT(*) DESC")
                .setMaxResults(10)
                .getResultList();

        List<CommandMostUsedData> result = new ArrayList<>();

        for (Object[] resultItem : scripts) {
            Command command = (Command) resultItem[0];
            Long count = (Long) resultItem[1];
            log.info(String.format("Result: source %s, script %s, used %d times", command.getCommandSource().getName(), command.getScript(), count));

            CommandData commandData = commandDataFromCommandEntity(command);

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
        List<Object[]> scripts = em.createQuery("select se.command, MAX(se.startTime) from CommandExecution se GROUP BY se.command ORDER BY MAX(se.startTime) DESC")
                .setMaxResults(10)
                .getResultList();

        List<CommandLastUsedData> result = new ArrayList<>();

        for (Object[] resultItem : scripts) {
            Command command = (Command) resultItem[0];
            Timestamp lastUsage = (Timestamp) resultItem[1];
            log.info(String.format("Result: source %s, script %s, last used %s", command.getCommandSource().getName(), command.getName(), lastUsage));

            CommandData commandData = commandDataFromCommandEntity(command);

            CommandLastUsedData commandLastUsedData = new CommandLastUsedData();
            commandLastUsedData.setCommandData(commandData);
            commandLastUsedData.setLastUsed(new Date(lastUsage.getTime()));
            result.add(commandLastUsedData);
        }

        return result;
    }

    /**
     * Returns the script session ID
     * @param commandId
     * @return
     */
    @Path("/sources/{commandSourceId}/{commandId}/run-async")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public String runAsync(@PathParam("commandSourceId") Long commandSourceId, @PathParam("commandId") Long commandId) {
        Command command = em.createQuery("select c from Command c where c.commandSource.id = :id AND c.id = :commandId", Command.class)
                .setParameter("id", commandSourceId)
                .setParameter("commandId", commandId)
                .getSingleResult();

        CommandSource commandSource = command.getCommandSource();

        log.info(String.format("Running script %s from script source ID=%d, NAME=%s async", command.getScript(), commandSource.getId(), commandSource.getName()));

        AbstractCommandType scriptType = CommandTypes.newForScriptSource(commandSource);

        CommandExecution commandExecution = new CommandExecution();
        commandExecution.setCommand(command);
        em.persist(commandExecution);

        log.debug("Started execution as SCRIPT_EXECUTION.ID=%d".formatted(commandExecution.getId()));

        // Just trigger here, return immediately
        AbstractCommandRunner scriptExecutionRunner = scriptType.createScriptExecution(command.getScript(), Long.toString(commandExecution.getId()));
        scriptExecutionRunner.updateEntity(commandExecution);
        scriptExecutionRunner.addStatusChangeListener(scriptExecutionRunner1 -> {
            log.debug(String.format("Script execution ID=%s status changed - updating in DB", scriptExecutionRunner1.getScriptExecutionID()));
            commandExecutionService.updateScriptExecution(scriptExecutionRunner1);
        });
        commandRunSessions.createForActiveExecution(scriptExecutionRunner);
        runAsyncInExecutor(scriptExecutionRunner);

        // Explicitly wrap as JSON string (I don't know yet why it's not done automatically)
        return "\"" + scriptExecutionRunner.getScriptExecutionID() + "\"";
    }

}
