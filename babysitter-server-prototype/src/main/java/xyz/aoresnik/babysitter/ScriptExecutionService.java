package xyz.aoresnik.babysitter;

import io.smallrye.mutiny.Uni;
import xyz.aoresnik.babysitter.entity.ScriptExecution;
import xyz.aoresnik.babysitter.script.AbstractScriptRunner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class ScriptExecutionService {
    @Inject
    EntityManager em;

    @Transactional
    public void updateScriptExecution(AbstractScriptRunner scriptExecutionRunner) {
        ScriptExecution scriptExecution = em.find(ScriptExecution.class, Long.parseLong(scriptExecutionRunner.getScriptExecutionID()));
        scriptExecutionRunner.updateEntity(scriptExecution);
    }

    public ScriptExecution getScriptExecution(String sessionId) {
        return em.find(ScriptExecution.class, Long.parseLong(sessionId));
    }
}