package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;

import java.util.List;
import java.util.Objects;

public class ListUserProfilPicturesUseCase implements UseCase<UserProfilePictures, ListUserProfilPicturesCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;
    private final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository;

    public ListUserProfilPicturesUseCase(final UserProfilePictureRepository userProfilePictureRepository,
                                         final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
        this.userProfilPictureFeaturedRepository = Objects.requireNonNull(userProfilPictureFeaturedRepository);
    }

    @Override
    public UserProfilePictures execute(final ListUserProfilPicturesCommand command) {
        final List<? extends UserProfilePictureIdentifier> userProfilePictureIdentifiers = userProfilePictureRepository.listByUserPseudoAndMediaType(command.userPseudo(), command.mediaType());
        if (userProfilePictureIdentifiers.isEmpty()) {
            return DomainUserProfilePictures.newBuilder().withNoPictureToSelect().build();
        }
        try {
            return userProfilPictureFeaturedRepository.getFeatured(command.userPseudo())
                    .map(userProfilePictureIdentifierFeatured ->
                            DomainUserProfilePictures.newBuilder()
                                    .withFeaturedStateSelected(userProfilePictureIdentifiers, userProfilePictureIdentifierFeatured).build())
                    .orElseGet(() -> DomainUserProfilePictures.newBuilder()
                            .withFeaturedStateNotSelectedYet(userProfilePictureIdentifiers).build());
        } catch (final UserProfilPictureFeaturedRepositoryException userProfilPictureFeaturedRepositoryException) {
            return DomainUserProfilePictures.newBuilder()
                    .withFeaturedStateInErrorWhenRetrieving(userProfilePictureIdentifiers).build();
        }
    }

}
