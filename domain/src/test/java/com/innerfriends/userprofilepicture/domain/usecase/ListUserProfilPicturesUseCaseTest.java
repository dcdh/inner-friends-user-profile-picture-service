package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListUserProfilPicturesUseCaseTest {

    @Test
    public void should_list_user_profile_pictures() {
        // Given
        final UserProfilePictureRepository userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        doReturn(Collections.singletonList(userProfilePicture)).when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat(new ListUserProfilPicturesUseCase(userProfilePictureRepository).execute(listUserProfilPicturesCommand)).isEqualTo(Collections.singletonList(userProfilePicture));
    }

}
