package xyz.aoresnik.babysitter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
public class ScriptExecutionInitialStateData {
    private boolean scriptRun;

    private boolean scriptCompleted;

    private Integer exitCode;

    private String errorText;

    private byte[] initialConsoleData;
}
