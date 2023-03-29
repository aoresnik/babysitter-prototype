package xyz.aoresnik.babysitter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 */
@Path("/api/v1/scripts")
public class ScriptsResource {

    public static final java.nio.file.Path SCRIPTS_DIR = java.nio.file.Path.of("babysitter", "scripts");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello()
    {
        File scriptsDir = SCRIPTS_DIR.toFile();
        File[] files = scriptsDir.listFiles(file -> file.getName().endsWith(".sh"));
        return Arrays.stream(files).map(File::getName).collect(Collectors.joining("\n"));
    }
}
