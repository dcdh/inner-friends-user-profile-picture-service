package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;

import java.util.Objects;

public class GetFeaturedUserProfilePictureUseCase implements UseCase<UserProfilePicture, GetFeaturedUserProfilePictureCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;
    private final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository;

    public GetFeaturedUserProfilePictureUseCase(final UserProfilePictureRepository userProfilePictureRepository,
                                                final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
        this.userProfilPictureFeaturedRepository = Objects.requireNonNull(userProfilPictureFeaturedRepository);
    }

    @Override
    public UserProfilePicture execute(final GetFeaturedUserProfilePictureCommand command) {
        return userProfilPictureFeaturedRepository.getFeatured(command.userPseudo())
                .map(DefaultUserProfilePicture::new)
                .orElseGet(() -> userProfilePictureRepository.getFirst(command.userPseudo(), command.mediaType())
                        .map(DefaultUserProfilePicture::new)
                        .orElseThrow(() -> new UserProfilePictureNotAvailableYetException(command.userPseudo())));
    }

    static final class DefaultUserProfilePicture implements UserProfilePicture {

        private final UserProfilePictureIdentifier userProfilePictureIdentifier;

        public DefaultUserProfilePicture(final UserProfilePictureIdentifier userProfilePictureIdentifier) {
            this.userProfilePictureIdentifier = Objects.requireNonNull(userProfilePictureIdentifier);
        }

        @Override
        public boolean isFeatured() {
            return true;
        }

        @Override
        public UserPseudo userPseudo() {
            return userProfilePictureIdentifier.userPseudo();
        }

        @Override
        public SupportedMediaType mediaType() {
            return userProfilePictureIdentifier.mediaType();
        }

        @Override
        public VersionId versionId() {
            return userProfilePictureIdentifier.versionId();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DefaultUserProfilePicture)) return false;
            DefaultUserProfilePicture that = (DefaultUserProfilePicture) o;
            return Objects.equals(userProfilePictureIdentifier, that.userProfilePictureIdentifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userProfilePictureIdentifier);
        }
    }

}
