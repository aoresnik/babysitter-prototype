package xyz.aoresnik.babysitter;

import org.jboss.logging.Logger;
import xyz.aoresnik.babysitter.entity.Command;
import xyz.aoresnik.babysitter.entity.CommandSource;
import xyz.aoresnik.babysitter.script.AbstractCommandType;
import xyz.aoresnik.babysitter.script.CommandTypes;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
public class CommandsService {

    @Inject
    Logger log;

    @Inject
    EntityManager em;

    /**
     * Refreshes the list of commands in the database from the sources.
     */
    @Transactional
    public void refreshCommands() {
        log.info("Refreshing the list of commands in the database from the sources.");
        List<CommandSource> commandSources = em.createQuery("select ss from CommandSource ss", CommandSource.class).getResultList();

        for (CommandSource commandSource : commandSources) {
            log.info(String.format("Detecting commands in source: %s", commandSource));
            AbstractCommandType scriptType = CommandTypes.newForScriptSource(commandSource);

            List<String> scriptIds = scriptType.getScripts();
            log.debug("The source enumerated commands: " + scriptIds);

            scriptIds.forEach(filename -> {
                Command command = new Command();
                command.setCommandSource(commandSource);
                command.setName(filename);
                command.setScript(filename);
                em.persist(command);
            });
        }
    }
}
