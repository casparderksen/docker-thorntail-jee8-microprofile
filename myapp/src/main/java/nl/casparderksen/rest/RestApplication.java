package nl.casparderksen.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(
        title = "My application",
        version = "1.0.0",
        contact = @Contact(
                name = "Caspar Derksen",
                url = "https://github.com/casparderksen/docker-thorntail-jee8-microprofile")
        ),
        servers = {
                @Server(url = "/", description = "localhost")
        }
)
@ApplicationPath("/api")
public class RestApplication extends Application {
}
