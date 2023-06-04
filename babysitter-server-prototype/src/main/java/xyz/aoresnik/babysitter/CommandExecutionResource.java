package xyz.aoresnik.babysitter;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.CommandExecutionData;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
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
        List<ScriptExecution> scriptExecutions = em.createQuery("select se from ScriptExecution se", ScriptExecution.class).getResultList();

        List<CommandExecutionData> result = new ArrayList<>();

        for (ScriptExecution scriptExecution : scriptExecutions) {
            CommandExecutionData commandExecutionData = scriptExecutionDataFromEntity(scriptExecution);

            result.add(commandExecutionData);
        }

        return result;
    }

    @Path("/{executionId}/transcript")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public byte[] getRawTranscript(@PathParam("executionId") String executionId) {
        ScriptExecution scriptExecution = commandExecutionService.getScriptExecution(executionId);
        AbstractCommandRunner scriptRunner1 = CommandTypes.newForScriptSource(scriptExecution.getScriptSource()).forInactiveScriptExecution(scriptExecution);
        return scriptRunner1.getResult();
    }

    private static CommandExecutionData scriptExecutionDataFromEntity(ScriptExecution scriptExecution) {
        CommandExecutionData commandExecutionData = new CommandExecutionData();
        commandExecutionData.setScriptExecutionId(String.valueOf(scriptExecution.getId()));

        commandExecutionData.setScriptRun(scriptExecution.isScriptRun());
        commandExecutionData.setScriptCompleted(scriptExecution.isScriptCompleted());
        commandExecutionData.setExitCode(scriptExecution.getExitCode());
        commandExecutionData.setErrorText(scriptExecution.getErrorText());
        commandExecutionData.setStartTime(scriptExecution.getStartTime() != null ? scriptExecution.getStartTime() : null);
        commandExecutionData.setEndTime(scriptExecution.getEndTime() != null ? scriptExecution.getEndTime() : null);

        commandExecutionData.setScriptId(scriptExecution.getScriptId());
        commandExecutionData.setScriptSourceId(String.valueOf(scriptExecution.getScriptSource().getId()));
        commandExecutionData.setScriptSourceName(scriptExecution.getScriptSource().getName());
        return commandExecutionData;
    }

}
