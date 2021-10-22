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
                .map(userProfilePictureIdentifier -> new DomainUserProfilePicture(userProfilePictureIdentifier, true))
                .orElseGet(() -> userProfilePictureRepository.getFirst(command.userPseudo(), command.mediaType())
                        .map(userProfilePictureIdentifier -> new DomainUserProfilePicture(userProfilePictureIdentifier, true))
                        .orElseThrow(() -> new UserProfilePictureNotAvailableYetException(command.userPseudo())));
    }

}
