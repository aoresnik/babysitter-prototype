package xyz.aoresnik.babysitter.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class CommandData {
    private Long commandSourceId;

    private String commandSourceName;

    /**
     * For scripts in script dir contains the directory name.
     */
    private String commandId;
}
