package xyz.aoresnik.babysitter;

import xyz.aoresnik.babysitter.entity.CommandExecution;
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
        CommandExecution commandExecution = em.find(CommandExecution.class, Long.parseLong(scriptExecutionRunner.getScriptExecutionID()));
        scriptExecutionRunner.updateEntity(commandExecution);
    }

    public CommandExecution getScriptExecution(String executionId) {
        return em.find(CommandExecution.class, Long.parseLong(executionId));
    }
}
