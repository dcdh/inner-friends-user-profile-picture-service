package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilPictureFeaturedRepository;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class MarkUserProfilePictureAsFeaturedUseCaseTest {

    @Test
    public void should_mark_user_profile_picture_as_featured() {
        // Given
        final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository = mock(UserProfilPictureFeaturedRepository.class);
        final MarkUserProfilePictureAsFeaturedCommand markUserProfilePictureAsFeaturedCommand = mock(MarkUserProfilePictureAsFeaturedCommand.class);
        final UserProfilePictureIdentifier userProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        doReturn(userProfilePictureIdentifier).when(userProfilPictureFeaturedRepository).markAsFeatured(markUserProfilePictureAsFeaturedCommand);

        // When && Then
        assertThat(new MarkUserProfilePictureAsFeaturedUseCase(userProfilPictureFeaturedRepository).execute(markUserProfilePictureAsFeaturedCommand))
                .isEqualTo(new MarkUserProfilePictureAsFeaturedUseCase.DefaultUserProfilePicture(userProfilePictureIdentifier));
    }

}
