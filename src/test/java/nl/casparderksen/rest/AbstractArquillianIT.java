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

    URL ping() {
        return url("api/ping");
    }

    URL config(String key) {
        return url("api/config/" + key);
    }

    URL health() {
        return url("health");
    }

    URL metrics() {
        return url("metrics");
    }

    URL metric(String name) {
        return url("metrics/application/" + name);
    }

    URL swaggerUI() {
        return url("api/openapi-ui");
    }
}