package com.innerfriends.userprofilepicture.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class WidthTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Width.class).verify();
    }

}
