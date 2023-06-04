package xyz.aoresnik.babysitter;

import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.script.AbstractCommandRunner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class CommandExecutionService {
    @Inject
    EntityManager em;

    @Transactional
    public void updateScriptExecution(AbstractCommandRunner scriptExecutionRunner) {
        ScriptExecution scriptExecution = em.find(ScriptExecution.class, Long.parseLong(scriptExecutionRunner.getScriptExecutionID()));
        scriptExecutionRunner.updateEntity(scriptExecution);
    }

    public ScriptExecution getScriptExecution(String executionId) {
        return em.find(ScriptExecution.class, Long.parseLong(executionId));
    }
}
