package xyz.aoresnik.babysitter.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
public class ScriptExecutionInitialStateData extends ScriptExecutionData {

    private byte[] initialConsoleData;
}
