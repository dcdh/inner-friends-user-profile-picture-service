package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListUserProfilPicturesUseCase implements UseCase<List<? extends UserProfilePicture>, ListUserProfilPicturesCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;
    private final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository;

    public ListUserProfilPicturesUseCase(final UserProfilePictureRepository userProfilePictureRepository,
                                         final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
        this.userProfilPictureFeaturedRepository = Objects.requireNonNull(userProfilPictureFeaturedRepository);
    }

    @Override
    public List<? extends UserProfilePicture> execute(final ListUserProfilPicturesCommand command) {
        final List<? extends UserProfilePictureIdentifier> userProfilePictureIdentifiers = userProfilePictureRepository.listByUserPseudoAndMediaType(command.userPseudo(), command.mediaType());
        if (userProfilePictureIdentifiers.isEmpty()) {
            return Collections.emptyList();
        }
        final UserProfilePictureIdentifier userProfilePictureIdentifierFeatured = userProfilPictureFeaturedRepository.getFeatured(command.userPseudo())
                .orElseGet(() -> userProfilePictureIdentifiers.get(0));
        return userProfilePictureIdentifiers
                .stream()
                .map(userProfilePictureIdentifier -> new DefaultUserProfilePicture(userProfilePictureIdentifier, userProfilePictureIdentifierFeatured))
                .collect(Collectors.toList());
    }

    static final class DefaultUserProfilePicture implements UserProfilePicture {

        private final UserPseudo userPseudo;
        private final SupportedMediaType mediaType;
        private final VersionId versionId;
        private final boolean isFeatured;

        public DefaultUserProfilePicture(final UserProfilePictureIdentifier userProfilePictureIdentifier,
                                         final UserProfilePictureIdentifier userProfilePictureIdentifierFeatured) {
            Objects.requireNonNull(userProfilePictureIdentifier);
            Objects.requireNonNull(userProfilePictureIdentifierFeatured);
            this.userPseudo = userProfilePictureIdentifier.userPseudo();
            this.mediaType = userProfilePictureIdentifier.mediaType();
            this.versionId = userProfilePictureIdentifier.versionId();
            this.isFeatured = userProfilePictureIdentifier.isEqual(userProfilePictureIdentifierFeatured);
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
        public boolean isFeatured() {
            return isFeatured;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DefaultUserProfilePicture)) return false;
            DefaultUserProfilePicture that = (DefaultUserProfilePicture) o;
            return isFeatured == that.isFeatured &&
                    Objects.equals(userPseudo, that.userPseudo) &&
                    mediaType == that.mediaType &&
                    Objects.equals(versionId, that.versionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userPseudo, mediaType, versionId, isFeatured);
        }
    }
}
