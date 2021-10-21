package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GetFeaturedUserProfilePictureUseCaseTest {

    private UserProfilePictureRepository userProfilePictureRepository;
    private UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository;

    @BeforeEach
    public void setup() {
        userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        userProfilPictureFeaturedRepository = mock(UserProfilPictureFeaturedRepository.class);
    }

    @Test
    public void should_get_featured_user_profile_picture() {
        // Given
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = new GetFeaturedUserProfilePictureCommand(
                userPseudo,
                SupportedMediaType.IMAGE_JPEG);
        doReturn(Optional.of(userProfilePicture)).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);

        // When && Then
        assertThat(new GetFeaturedUserProfilePictureUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(getFeaturedUserProfilePictureCommand)).isEqualTo(new GetFeaturedUserProfilePictureUseCase.DefaultUserProfilePicture(userProfilePicture));
    }

    @Test
    public void should_get_first_when_no_user_profile_picture_has_been_marked_as_featured() {
        // Given
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = new GetFeaturedUserProfilePictureCommand(
                userPseudo,
                SupportedMediaType.IMAGE_JPEG);
        doReturn(Optional.empty()).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);
        doReturn(Optional.of(userProfilePicture)).when(userProfilePictureRepository).getFirst(userPseudo, SupportedMediaType.IMAGE_JPEG);

        // When && Then
        assertThat(new GetFeaturedUserProfilePictureUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(getFeaturedUserProfilePictureCommand)).isEqualTo(new GetFeaturedUserProfilePictureUseCase.DefaultUserProfilePicture(userProfilePicture));
    }

    @Test
    public void should_throw_user_profile_picture_not_available_yet_exception_when_no_one_has_been_saved_yet() {
        // Given
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = new GetFeaturedUserProfilePictureCommand(
                userPseudo,
                SupportedMediaType.IMAGE_JPEG);
        doReturn(Optional.empty()).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);
        doReturn(Optional.empty()).when(userProfilePictureRepository).getFirst(userPseudo, SupportedMediaType.IMAGE_JPEG);

        // When && Then
        assertThatThrownBy(() -> new GetFeaturedUserProfilePictureUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(getFeaturedUserProfilePictureCommand))
                .isInstanceOf(UserProfilePictureNotAvailableYetException.class);
    }
}
