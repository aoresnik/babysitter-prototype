package xyz.aoresnik.babysitter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Included in the OpenAPI spec, because it's useful to have the interface class available in the frontend.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data used via websocket for sending interaction coomands from user from frontend to backend")
public class CommandInputData {
    private String inputData;
}
