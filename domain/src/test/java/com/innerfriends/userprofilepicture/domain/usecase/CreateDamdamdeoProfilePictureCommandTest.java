package com.innerfriends.userprofilepicture.domain.usecase;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDamdamdeoProfilePictureCommandTest {

    @Test
    public void should_user_pseudo_return_Damdamdeo() {
        assertThat(new CreateDamdamdeoProfilePictureCommand().userPseudo().pseudo()).isEqualTo("Damdamdeo");
    }

}
