package xyz.aoresnik.babysitter;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.data.ScriptData;
import xyz.aoresnik.babysitter.data.ScriptExecutionData;
import xyz.aoresnik.babysitter.entity.ScriptExecution;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Path("/api/v1/executions")
public class ScriptExecutionResource {
    @Inject
    Logger log;

    @Inject
    EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScriptExecutionData> getScripts() {
        log.info("Reading the list of scripts");
        List<ScriptExecution> scriptExecutions = em.createQuery("select se from ScriptExecution se", ScriptExecution.class).getResultList();

        List<ScriptExecutionData> result = new ArrayList<>();

        for (ScriptExecution scriptExecution : scriptExecutions) {
            ScriptExecutionData scriptExecutionData = scriptExecutionDataFromEntity(scriptExecution);

            result.add(scriptExecutionData);
        }

        return result;
    }

    private static ScriptExecutionData scriptExecutionDataFromEntity(ScriptExecution scriptExecution) {
        ScriptExecutionData scriptExecutionData = new ScriptExecutionData();
        scriptExecutionData.setScriptExecutionId(String.valueOf(scriptExecution.getId()));

        scriptExecutionData.setScriptRun(scriptExecution.isScriptRun());
        scriptExecutionData.setScriptCompleted(scriptExecution.isScriptCompleted());
        scriptExecutionData.setExitCode(scriptExecution.getExitCode());
        scriptExecutionData.setErrorText(scriptExecution.getErrorText());
        scriptExecutionData.setStartTime(scriptExecution.getStartTime() != null ? scriptExecution.getStartTime() : null);
        scriptExecutionData.setEndTime(scriptExecution.getEndTime() != null ? scriptExecution.getEndTime() : null);

        scriptExecutionData.setScriptId(scriptExecution.getScriptId());
        scriptExecutionData.setScriptSourceId(String.valueOf(scriptExecution.getScriptSource().getId()));
        scriptExecutionData.setScriptSourceName(scriptExecution.getScriptSource().getName());
        return scriptExecutionData;
    }

}
