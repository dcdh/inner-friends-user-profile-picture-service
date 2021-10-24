package com.innerfriends.userprofilepicture.infrastructure.imaging;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class MaxDimensionTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(MaxDimension.class).verify();
    }

}
