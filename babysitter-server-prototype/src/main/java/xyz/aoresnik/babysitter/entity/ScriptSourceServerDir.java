package xyz.aoresnik.babysitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "SCRIPT_SOURCE_SERVER_DIR")
public class ScriptSourceServerDir {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;

    @Column(name = "DIR_NAME")
    String dirname;

    @OneToOne
    @JoinColumn(name = "SCRIPT_SOURCE_ID")
    ScriptSource scriptSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDirname() {
        return dirname;
    }

    public void setDirname(String dirname) {
        this.dirname = dirname;
    }

    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(ScriptSource scriptSource) {
        this.scriptSource = scriptSource;
    }

    @Override
    public String toString() {
        return "ScriptSourceServerDir{" +
                "id=" + id +
                ", dirname='" + dirname + '\'' +
                '}';
    }
}
