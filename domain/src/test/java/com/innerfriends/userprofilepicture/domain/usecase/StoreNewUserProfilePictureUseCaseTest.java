package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureSaved;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class StoreNewUserProfilePictureUseCaseTest {

    @Test
    public void should_store_new_user_profile_picture() {
        // Given
        final UserProfilePictureRepository userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        final StoreNewUserProfilePictureCommand storeNewUserProfilePictureCommand = mock(StoreNewUserProfilePictureCommand.class);
        final UserProfilePictureSaved userProfilePictureSaved = mock(UserProfilePictureSaved.class);
        doReturn(userProfilePictureSaved).when(userProfilePictureRepository).storeNewUserProfilePicture(storeNewUserProfilePictureCommand);

        // When && Then
        assertThat(new StoreNewUserProfilePictureUseCase(userProfilePictureRepository).execute(storeNewUserProfilePictureCommand)).isEqualTo(userProfilePictureSaved);
    }

}
