package xyz.aoresnik.babysitter.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class ScriptExecutionData {
    private String scriptExecutionId;

    private String scriptSourceId;

    private String scriptSourceName;

    private String scriptId;

    private boolean scriptRun;

    private boolean scriptCompleted;

    private Integer exitCode;

    private String errorText;

}

