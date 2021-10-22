package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListUserProfilPicturesUseCaseTest {

    private UserProfilePictureRepository userProfilePictureRepository;
    private UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository;

    @BeforeEach
    public void setup() {
        userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        userProfilPictureFeaturedRepository = mock(UserProfilPictureFeaturedRepository.class);
    }

    private static final class TestUserPseudo implements UserPseudo {

        private final String userPseudo;

        public TestUserPseudo(final String userPseudo) {
            this.userPseudo = userPseudo;
        }

        @Override
        public String pseudo() {
            return userPseudo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestUserPseudo)) return false;
            TestUserPseudo that = (TestUserPseudo) o;
            return Objects.equals(userPseudo, that.userPseudo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userPseudo);
        }
    }

    private static final class TestVersionId implements VersionId {

        private final String version;

        public TestVersionId(final String version) {
            this.version = version;
        }

        @Override
        public String version() {
            return version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestVersionId)) return false;
            TestVersionId that = (TestVersionId) o;
            return Objects.equals(version, that.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(version);
        }
    }

    private static final class TestUserProfilePictureIdentifier implements UserProfilePictureIdentifier {

        private final String userPseudo;

        public TestUserProfilePictureIdentifier(final String userPseudo) {
            this.userPseudo = userPseudo;
        }

        @Override
        public UserPseudo userPseudo() {
            return new TestUserPseudo(userPseudo);
        }

        @Override
        public SupportedMediaType mediaType() {
            return SupportedMediaType.IMAGE_JPEG;
        }

        @Override
        public VersionId versionId() {
            return new TestVersionId("v0");
        }

    }

    @Test
    public void should_list_user_profile_pictures_return_empty_when_no_user_profile_pictures_are_presents() {
        // Given
        final UserPseudo userPseudo = mock(UserPseudo.class);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        doReturn(Collections.emptyList()).when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat(new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository).execute(listUserProfilPicturesCommand))
                .isEqualTo(new DomainUserProfilePictures(Collections.emptyList(), FeatureState.NO_PICTURE_TO_SELECT));
    }

    @Test
    public void should_use_first_user_profile_picture_as_featured_when_no_user_profile_picture_marked_as_featured() {
        // Given
        final UserProfilePictureIdentifier firstUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("first");
        final UserProfilePictureIdentifier secondUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("second");
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(Optional.empty()).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        doReturn(List.of(firstUserProfilePictureIdentifier, secondUserProfilePictureIdentifier))
                .when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat(new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(listUserProfilPicturesCommand))
                .isEqualTo(
                        new DomainUserProfilePictures(
                                List.of(
                                        new DomainUserProfilePicture(firstUserProfilePictureIdentifier, true),
                                        new DomainUserProfilePicture(secondUserProfilePictureIdentifier, false)),
                                FeatureState.NOT_SELECTED_YET_GET_FIRST_ONE)
                );
    }

    @Test
    public void should_use_featured_user_profile_picture() {
        // Given
        final UserProfilePictureIdentifier firstUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("first");
        final UserProfilePictureIdentifier secondUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("second");
        final UserProfilePictureIdentifier featuredUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("featured");
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(Optional.of(featuredUserProfilePictureIdentifier)).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        doReturn(List.of(firstUserProfilePictureIdentifier, secondUserProfilePictureIdentifier, featuredUserProfilePictureIdentifier))
                .when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat(new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(listUserProfilPicturesCommand))
                .isEqualTo(
                        new DomainUserProfilePictures(
                                List.of(
                                        new DomainUserProfilePicture(firstUserProfilePictureIdentifier, false),
                                        new DomainUserProfilePicture(secondUserProfilePictureIdentifier, false),
                                        new DomainUserProfilePicture(featuredUserProfilePictureIdentifier, true)),
                                FeatureState.SELECTED)
                );
    }

    @Test
    public void should_handle_user_profil_picture_featured_repository_exception() {
        // Given
        final UserProfilePictureIdentifier firstUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("first");
        final UserProfilePictureIdentifier secondUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("second");
        final UserProfilePictureIdentifier featuredUserProfilePictureIdentifier = new TestUserProfilePictureIdentifier("featured");
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doThrow(new UserProfilPictureFeaturedRepositoryException()).when(userProfilPictureFeaturedRepository).getFeatured(userPseudo);
        final SupportedMediaType mediaType = SupportedMediaType.IMAGE_JPEG;
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = new ListUserProfilPicturesCommand(userPseudo, mediaType);
        doReturn(List.of(firstUserProfilePictureIdentifier, secondUserProfilePictureIdentifier, featuredUserProfilePictureIdentifier))
                .when(userProfilePictureRepository).listByUserPseudoAndMediaType(userPseudo, mediaType);

        // When && Then
        assertThat(new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository)
                .execute(listUserProfilPicturesCommand))
                .isEqualTo(
                        new DomainUserProfilePictures(
                                List.of(
                                        new DomainUserProfilePicture(firstUserProfilePictureIdentifier, false),
                                        new DomainUserProfilePicture(secondUserProfilePictureIdentifier, false),
                                        new DomainUserProfilePicture(featuredUserProfilePictureIdentifier, false)),
                                FeatureState.IN_ERROR_WHEN_RETRIEVING)
                );
    }

}
