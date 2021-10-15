package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UserProfilePictureDTOTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UserProfilePictureDTO.class).verify();
    }

}
