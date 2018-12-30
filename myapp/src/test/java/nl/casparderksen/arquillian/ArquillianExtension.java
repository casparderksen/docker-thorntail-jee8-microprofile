package nl.casparderksen.arquillian;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * Adds test libraries to the deployment for in-container tests.
 */
@SuppressWarnings("WeakerAccess")
public class ArquillianExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, AssertjAuxiliaryArchiveAppender.class);
    }

    public static class AssertjAuxiliaryArchiveAppender implements AuxiliaryArchiveAppender {
        @Override
        public JavaArchive createAuxiliaryArchive() {
            return ShrinkWrap.create(JavaArchive.class)
                    .addPackages(true, "org.assertj", "io.restassured");
        }
    }
}