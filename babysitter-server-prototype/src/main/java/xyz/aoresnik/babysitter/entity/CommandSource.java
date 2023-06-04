package xyz.aoresnik.babysitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "COMMAND_SOURCE")
public class CommandSource {
    // TODO: migrate to GUID for this and all IDs that are sent via REST (requires Quarkus 3 with JEE 10 and JPA 3.1)
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @OneToOne(mappedBy = "commandSource", optional = true)
    private CommandSourceServerDir scriptSourceServerDir;

    @OneToOne(mappedBy = "commandSource", optional = true)
    private CommandSourceSSHDir commandSourceSSHDir;

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

    public CommandSourceServerDir getScriptSourceServerDir() {
        return scriptSourceServerDir;
    }

    public void setScriptSourceServerDir(CommandSourceServerDir scriptSourceServerDir) {
        this.scriptSourceServerDir = scriptSourceServerDir;
    }

    public CommandSourceSSHDir getScriptSourceSSHDir() {
        return commandSourceSSHDir;
    }

    public void setScriptSourceSSHDir(CommandSourceSSHDir commandSourceSSHDir) {
        this.commandSourceSSHDir = commandSourceSSHDir;
    }

    @Override
    public String toString() {
        return "CommandSource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", scriptSourceServerDir=" + scriptSourceServerDir +
                '}';
    }
}
