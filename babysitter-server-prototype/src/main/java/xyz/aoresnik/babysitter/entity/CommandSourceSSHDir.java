package xyz.aoresnik.babysitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "COMMAND_SOURCE_SSH_DIR")
public class CommandSourceSSHDir {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;

    @Column(name = "DIR_NAME")
    String dirname;

    @OneToOne
    @JoinColumn(name = "COMMAND_SOURCE_ID")
    CommandSource commandSource;

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

    public CommandSource getScriptSource() {
        return commandSource;
    }

    public void setScriptSource(CommandSource commandSource) {
        this.commandSource = commandSource;
    }

    public byte[] getSshConfig() {
        return sshConfig;
    }

    public void setSshConfig(byte[] sshConfig) {
        this.sshConfig = sshConfig;
    }

    @Override
    public String toString() {
        return "CommandSourceServerDir{" +
                "id=" + id +
                ", dirname='" + dirname + '\'' +
                '}';
    }
}
