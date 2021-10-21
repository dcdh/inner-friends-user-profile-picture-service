package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;

import java.util.Objects;

public class MarkUserProfilePictureAsFeaturedUseCase implements UseCase<UserProfilePicture, MarkUserProfilePictureAsFeaturedCommand> {

    private final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository;

    public MarkUserProfilePictureAsFeaturedUseCase(final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository) {
        this.userProfilPictureFeaturedRepository = Objects.requireNonNull(userProfilPictureFeaturedRepository);
    }

    @Override
    public UserProfilePicture execute(final MarkUserProfilePictureAsFeaturedCommand command) {
        return new DefaultUserProfilePicture(this.userProfilPictureFeaturedRepository.markAsFeatured(command));
    }

    public static final class DefaultUserProfilePicture implements UserProfilePicture {

        private final UserPseudo userPseudo;
        private final SupportedMediaType mediaType;
        private final VersionId versionId;

        public DefaultUserProfilePicture(final UserProfilePictureIdentifier userProfilePictureIdentifier) {
            this.userPseudo = userProfilePictureIdentifier.userPseudo();
            this.mediaType = userProfilePictureIdentifier.mediaType();
            this.versionId = userProfilePictureIdentifier.versionId();
        }

        @Override
        public boolean isFeatured() {
            return true;
        }

        @Override
        public UserPseudo userPseudo() {
            return userPseudo;
        }

        @Override
        public SupportedMediaType mediaType() {
            return mediaType;
        }

        @Override
        public VersionId versionId() {
            return versionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DefaultUserProfilePicture)) return false;
            DefaultUserProfilePicture that = (DefaultUserProfilePicture) o;
            return Objects.equals(userPseudo, that.userPseudo) &&
                    mediaType == that.mediaType &&
                    Objects.equals(versionId, that.versionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userPseudo, mediaType, versionId);
        }
    }
}
