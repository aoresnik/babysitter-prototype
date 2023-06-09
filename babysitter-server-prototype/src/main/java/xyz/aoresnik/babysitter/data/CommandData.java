package xyz.aoresnik.babysitter.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class CommandData {
    @JsonProperty(required = true)
    private Long commandSourceId;

    @JsonProperty(required = true)
    private String commandSourceName;

    /**
     * For scripts in script dir contains the directory name.
     */
    @JsonProperty(required = true)
    private String commandId;

    @JsonProperty(required = true)
    private String commandName;
}
