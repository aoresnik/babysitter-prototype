package xyz.aoresnik.babysitter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Real-time data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractScriptExecutionRTData {
    private String scriptExecutionID;

    private boolean scriptRun;

    private boolean scriptCompleted;

    private Integer exitCode;

    private String errorText;
}
