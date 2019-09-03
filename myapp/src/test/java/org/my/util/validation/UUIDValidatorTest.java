package org.my.util.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UUIDValidatorTest {

    private static final UUIDValidator validator= new UUIDValidator();

    @BeforeAll
    static void init() {
        validator.initialize(null);
    }

    @Test
    public void shouldBeValidUUID() {
        assertThat(validator.isValid("3a1757d0-cc9c-44cd-aee4-a5c0fd54d41b", null)).isTrue();
    }

    @Test
    public void shouldBeInvalidUUID() {
        assertThat(validator.isValid("3a1757d0", null)).isFalse();
    }
}