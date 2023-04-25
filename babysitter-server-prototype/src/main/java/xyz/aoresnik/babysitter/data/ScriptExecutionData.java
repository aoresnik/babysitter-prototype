package xyz.aoresnik.babysitter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecutionData {
    private boolean scriptRun;

    private boolean scriptCompleted;

    private Integer exitCode;

    private String errorText;
}
