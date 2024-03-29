package xyz.aoresnik.babysitter.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "COMMAND_EXECUTION")
public class CommandExecution {
    // TODO: migrate to GUID for this and all IDs that are sent via REST (requires Quarkus 3 with JEE 10 and JPA 3.1)
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean commandRun;

    @Column
    private boolean commandCompleted;

    @Column
    private Integer exitCode;

    @Column
    private String errorText;

    @Column
    private Timestamp startTime;

    @Column
    private Timestamp endTime;

    @ManyToOne
    @JoinColumn(name = "COMMAND_ID")
    private Command command;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCommandRun() {
        return commandRun;
    }

    public void setCommandRun(boolean scriptRun) {
        this.commandRun = scriptRun;
    }

    public boolean isCommandCompleted() {
        return commandCompleted;
    }

    public void setCommandCompleted(boolean scriptCompleted) {
        this.commandCompleted = scriptCompleted;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "CommandExecution{" +
                "id=" + id +
                ", commandRun=" + commandRun +
                ", commandCompleted=" + commandCompleted +
                ", exitCode=" + exitCode +
                ", errorText='" + errorText + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", command=" + command +
                '}';
    }
}
