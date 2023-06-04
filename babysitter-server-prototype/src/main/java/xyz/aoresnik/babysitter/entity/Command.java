package xyz.aoresnik.babysitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "COMMAND")
public class Command {
    // TODO: migrate to GUID for this and all IDs that are sent via REST (requires Quarkus 3 with JEE 10 and JPA 3.1)
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * NOTE: Name should be printed on the UI, but currently the UI sends the same value as scriptId as it prints on GUI,
     * so sending the value of {link @script} is a workaround.
     */
    @Column(name = "NAME")
    private String name;

    @Column(name = "SCRIPT")
    private String script;

    @ManyToOne
    @JoinColumn(name = "COMMAND_SOURCE_ID")
    private CommandSource commandSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String commandScript) {
        this.script = commandScript;
    }

    public CommandSource getCommandSource() {
        return commandSource;
    }

    public void setCommandSource(CommandSource commandSource) {
        this.commandSource = commandSource;
    }

    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", commandScript='" + script + '\'' +
                ", commandSource=" + commandSource +
                '}';
    }
}
