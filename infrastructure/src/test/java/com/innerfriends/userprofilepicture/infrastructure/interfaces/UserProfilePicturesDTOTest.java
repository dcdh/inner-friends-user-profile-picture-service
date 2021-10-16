package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UserProfilePicturesDTOTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UserProfilePicturesDTO.class).verify();
    }

}
