package xyz.aoresnik.babysitter.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@NoArgsConstructor
@XmlRootElement
public class CommandExecutionData {
    private String commandExecutionId;

    private String commandSourceId;

    private String commandSourceName;

    // TODO: change the frontend to use separate command name and command ID - it now uses command ID for both
    private String commandId;

    private boolean commandRun;

    private boolean commandCompleted;

    private Integer exitCode;

    private String errorText;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date endTime;

}

