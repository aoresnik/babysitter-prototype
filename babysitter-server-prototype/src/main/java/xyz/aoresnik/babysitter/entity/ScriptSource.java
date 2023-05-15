package xyz.aoresnik.babysitter.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "SCRIPT_SOURCE")
public class ScriptSource {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @OneToOne(mappedBy = "scriptSource", optional = true)
    private ScriptSourceServerDir scriptSourceServerDir;

    @OneToOne(mappedBy = "scriptSource", optional = true)
    private ScriptSourceSSHDir scriptSourceSSHDir;

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

    public ScriptSourceServerDir getScriptSourceServerDir() {
        return scriptSourceServerDir;
    }

    public void setScriptSourceServerDir(ScriptSourceServerDir scriptSourceServerDir) {
        this.scriptSourceServerDir = scriptSourceServerDir;
    }

    public ScriptSourceSSHDir getScriptSourceSSHDir() {
        return scriptSourceSSHDir;
    }

    public void setScriptSourceSSHDir(ScriptSourceSSHDir scriptSourceSSHDir) {
        this.scriptSourceSSHDir = scriptSourceSSHDir;
    }

    @Override
    public String toString() {
        return "ScriptSource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", scriptSourceServerDir=" + scriptSourceServerDir +
                '}';
    }
}