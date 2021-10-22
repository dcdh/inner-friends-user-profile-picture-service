package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class HazelcastCachedUserProfilePicturesTest {

    @Test
    public void should_verify_partial_equality() {
        EqualsVerifier.forClass(HazelcastCachedUserProfilePictures.class)
                .suppress(Warning.NONFINAL_FIELDS).verify();
    }

}
