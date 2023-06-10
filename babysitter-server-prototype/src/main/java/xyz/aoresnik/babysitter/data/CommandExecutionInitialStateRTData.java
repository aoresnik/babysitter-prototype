package xyz.aoresnik.babysitter.data;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Included in the OpenAPI spec, because it's useful to have the interface class available in the frontend.
 */
@Data
@XmlRootElement
@Schema(description = "Data used via websocket for first message in connection, sent from backend to frontend")
public class CommandExecutionInitialStateRTData extends AbstractScriptExecutionRTData {

    private byte[] initialConsoleData;
}
