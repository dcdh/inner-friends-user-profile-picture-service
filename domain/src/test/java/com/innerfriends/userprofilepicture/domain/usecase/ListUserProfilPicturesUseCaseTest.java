package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListUserProfilPicturesUseCaseTest {

    private UserProfilePictureRepository userProfilePictureRepository;
    private UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository;

    @BeforeEach
    public void setup() {
        userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        userProfilPictureFeaturedRepository = mock(UserProfilPictureFeaturedRepository.class);
    }

    @Test
    public void should_list_user_profile_pictures_return_empty_when_no_user_profile_pictures_are_presents() {
        // Given
        final UserPseudo userPseudo = mock(UserPseudo.class);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        doReturn(Collections.emptyList()).when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat(new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(listUserProfilPicturesCommand)).isEmpty();
    }

    // https://github.com/assertj/assertj-core/issues/772
    @SuppressWarnings("unchecked")
    @Test
    public void should_use_first_user_profile_picture_as_featured_when_no_user_profile_picture_marked_as_featured() {
        // Given
        final UserProfilePictureIdentifier firstUserProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        final UserProfilePictureIdentifier secondUserProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(Optional.empty()).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        doReturn(List.of(firstUserProfilePictureIdentifier, secondUserProfilePictureIdentifier))
                .when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat((List<UserProfilePicture>) new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(listUserProfilPicturesCommand))
                .containsExactly(
                        new ListUserProfilPicturesUseCase.DefaultUserProfilePicture(firstUserProfilePictureIdentifier, firstUserProfilePictureIdentifier),
                        new ListUserProfilPicturesUseCase.DefaultUserProfilePicture(secondUserProfilePictureIdentifier, firstUserProfilePictureIdentifier));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_use_featured_user_profile_picture() {
        // Given
        final UserProfilePictureIdentifier firstUserProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        final UserProfilePictureIdentifier secondUserProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        final UserProfilePictureIdentifier featuredUserProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(Optional.of(featuredUserProfilePictureIdentifier)).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        doReturn(List.of(firstUserProfilePictureIdentifier, secondUserProfilePictureIdentifier, featuredUserProfilePictureIdentifier))
                .when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat((List<UserProfilePicture>) new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(listUserProfilPicturesCommand))
                .containsExactly(
                        new ListUserProfilPicturesUseCase.DefaultUserProfilePicture(firstUserProfilePictureIdentifier, featuredUserProfilePictureIdentifier),
                        new ListUserProfilPicturesUseCase.DefaultUserProfilePicture(secondUserProfilePictureIdentifier, featuredUserProfilePictureIdentifier),
                        new ListUserProfilPicturesUseCase.DefaultUserProfilePicture(featuredUserProfilePictureIdentifier, featuredUserProfilePictureIdentifier));
    }

}
