package xyz.aoresnik.babysitter;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.CommandExecutionData;
import xyz.aoresnik.babysitter.entity.CommandExecution;
import xyz.aoresnik.babysitter.script.AbstractCommandRunner;
import xyz.aoresnik.babysitter.script.CommandTypes;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/executions")
public class CommandExecutionResource {
    @Inject
    Logger log;

    @Inject
    EntityManager em;

    @Inject
    CommandExecutionService commandExecutionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CommandExecutionData> getScripts() {
        log.info("Reading the list of scripts");
        List<CommandExecution> commandExecutions = em.createQuery("select ce from CommandExecution ce", CommandExecution.class).getResultList();

        List<CommandExecutionData> result = new ArrayList<>();

        for (CommandExecution commandExecution : commandExecutions) {
            CommandExecutionData commandExecutionData = scriptExecutionDataFromEntity(commandExecution);

            result.add(commandExecutionData);
        }

        return result;
    }

    @Path("/{executionId}/transcript")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public byte[] getRawTranscript(@PathParam("executionId") String executionId) {
        CommandExecution commandExecution = commandExecutionService.getScriptExecution(executionId);
        AbstractCommandRunner scriptRunner1 = CommandTypes.newForScriptSource(commandExecution.getCommand().getCommandSource()).forInactiveScriptExecution(commandExecution);
        return scriptRunner1.getResult();
    }

    private static CommandExecutionData scriptExecutionDataFromEntity(CommandExecution commandExecution) {
        CommandExecutionData commandExecutionData = new CommandExecutionData();
        commandExecutionData.setCommandExecutionId(String.valueOf(commandExecution.getId()));

        commandExecutionData.setCommandRun(commandExecution.isCommandRun());
        commandExecutionData.setCommandCompleted(commandExecution.isCommandCompleted());
        commandExecutionData.setExitCode(commandExecution.getExitCode());
        commandExecutionData.setErrorText(commandExecution.getErrorText());
        commandExecutionData.setStartTime(commandExecution.getStartTime() != null ? commandExecution.getStartTime() : null);
        commandExecutionData.setEndTime(commandExecution.getEndTime() != null ? commandExecution.getEndTime() : null);

        commandExecutionData.setCommandId(commandExecution.getCommand().getScript());
        commandExecutionData.setCommandSourceId(String.valueOf(commandExecution.getCommand().getCommandSource().getId()));
        commandExecutionData.setCommandSourceName(commandExecution.getCommand().getCommandSource().getName());
        return commandExecutionData;
    }

}
