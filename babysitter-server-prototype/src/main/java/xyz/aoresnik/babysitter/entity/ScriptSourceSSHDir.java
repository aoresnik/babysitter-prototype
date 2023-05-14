package xyz.aoresnik.babysitter.entity;

import org.hibernate.type.descriptor.sql.LobTypeMappings;

import javax.persistence.*;

@Entity
@Table(name = "SCRIPT_SOURCE_SSH_DIR")
public class ScriptSourceSSHDir {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;

    @Column(name = "DIR_NAME")
    String dirname;

    @OneToOne
    @JoinColumn(name = "SCRIPT_SOURCE_ID")
    ScriptSource scriptSource;

    @Column(name = "SSH_CONFIG")
    @Lob
    private byte[] sshConfig;

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

    public byte[] getSshConfig() {
        return sshConfig;
    }

    public void setSshConfig(byte[] sshConfig) {
        this.sshConfig = sshConfig;
    }

    @Override
    public String toString() {
        return "ScriptSourceServerDir{" +
                "id=" + id +
                ", dirname='" + dirname + '\'' +
                '}';
    }
}
