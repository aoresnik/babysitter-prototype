package xyz.aoresnik.babysitter.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
public class ScriptExecutionUpdateRTData extends AbstractScriptExecutionRTData {

    private byte[] incrementalConsoleData;

}