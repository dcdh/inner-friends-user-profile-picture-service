package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.ContentUserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GetContentUserProfilePictureUseCaseTest {

    @Test
    public void should_get_content_user_profile_picture_by_version() {
        // Given
        final UserProfilePictureRepository userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        final GetContentUserProfilePictureCommand getContentUserProfilePictureCommand = mock(GetContentUserProfilePictureCommand.class);
        final ContentUserProfilePicture contentUserProfilePicture = mock(ContentUserProfilePicture.class);
        doReturn(contentUserProfilePicture).when(userProfilePictureRepository).getContent(getContentUserProfilePictureCommand);

        // When && Then
        assertThat(new GetContentUserProfilePictureUseCase(userProfilePictureRepository).execute(getContentUserProfilePictureCommand))
                .isEqualTo(contentUserProfilePicture);
    }
}
