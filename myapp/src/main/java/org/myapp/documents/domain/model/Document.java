package org.myapp.documents.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Document {

    @NotNull
    private UUID id;

    @NotNull
    private String name;
}
