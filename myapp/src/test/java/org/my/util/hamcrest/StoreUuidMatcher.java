package org.my.util.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.UUID;

public class StoreUuidMatcher extends BaseMatcher<UUID> {

    private static final StoreUuidMatcher INSTANCE =  new StoreUuidMatcher();

    private UUID uuid;

    @SuppressWarnings("SameReturnValue")
    public static org.hamcrest.Matcher<UUID> storeUUID() {
        return INSTANCE;
    }

    public static String getUUID() {
        return INSTANCE.uuid.toString();
    }

    @Override
    public boolean matches(Object actual) {
        try {
            uuid = UUID.fromString(actual.toString());
            return true;
        }
        catch (IllegalArgumentException exception) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue("<ANY UUID>");
    }
}
