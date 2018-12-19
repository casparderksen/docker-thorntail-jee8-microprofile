package nl.casparderksen.rest;

import org.jboss.arquillian.test.api.ArquillianResource;

import java.net.MalformedURLException;
import java.net.URL;

abstract class AbstractArquillianIT {

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

    protected URL swaggerUI() {
        return url("api/openapi-ui");
    }
}
