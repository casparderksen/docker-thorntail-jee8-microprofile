package org.my.util.domain.service.mappers;

import java.util.UUID;

/**
 * Custom Mapstruct mapper between UUID and String.
 */
public class UUIDMapper {

    public String asString(UUID uuid) {
        return uuid.toString();
    }

    public UUID asUUID(String uuid) {
       return UUID.fromString(uuid);
    }
}
