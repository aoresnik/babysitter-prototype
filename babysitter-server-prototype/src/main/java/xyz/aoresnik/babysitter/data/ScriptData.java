package xyz.aoresnik.babysitter.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class ScriptData {
    private Long scriptSourceId;

    /**
     * For scripts in script dir contains the directory name.
     */
    private String scriptId;
}