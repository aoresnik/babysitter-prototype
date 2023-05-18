package xyz.aoresnik.babysitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "SCRIPT_EXECUTION")
public class ScriptExecution {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column
    private boolean scriptRun;

    @Column
    private boolean scriptCompleted;

    @Column
    private Integer exitCode;

    @Column
    private String errorText;

    @ManyToOne
    @JoinColumn(name = "SCRIPT_SOURCE_ID")
    private ScriptSource scriptSource;

    @Column(name = "SCRIPT_ID")
    private String scriptId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isScriptRun() {
        return scriptRun;
    }

    public void setScriptRun(boolean scriptRun) {
        this.scriptRun = scriptRun;
    }

    public boolean isScriptCompleted() {
        return scriptCompleted;
    }

    public void setScriptCompleted(boolean scriptCompleted) {
        this.scriptCompleted = scriptCompleted;
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

    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(ScriptSource scriptSource) {
        this.scriptSource = scriptSource;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    @Override
    public String toString() {
        return "ScriptExecution{" +
                "id=" + id +
                ", scriptRun=" + scriptRun +
                ", scriptCompleted=" + scriptCompleted +
                ", exitCode=" + exitCode +
                ", errorText='" + errorText + '\'' +
                ", scriptSource=" + scriptSource +
                ", scriptId='" + scriptId + '\'' +
                '}';
    }
}
