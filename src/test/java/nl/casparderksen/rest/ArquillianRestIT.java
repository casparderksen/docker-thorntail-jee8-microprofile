package nl.casparderksen.rest;

import org.jboss.arquillian.test.api.ArquillianResource;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class ArquillianRestIT {

    @ArquillianResource
    private URL deploymentURL;

    private URL url(String path) {
        try {
            return new URL(deploymentURL, path);
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected URL ping() {
        return url("api/ping");
    }

    protected URL config(String key) {
        return url("api/config/" + key);
    }

    protected URL health() {
        return url("health");
    }

    protected URL metrics() {
        return url("metrics");
    }

    protected URL metric(String name) {
        return url("metrics/application/" + name);
    }
}
