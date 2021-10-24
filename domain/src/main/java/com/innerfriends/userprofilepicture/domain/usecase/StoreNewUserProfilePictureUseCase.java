package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureDimensionValidator;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;

import java.util.Objects;

public class StoreNewUserProfilePictureUseCase implements UseCase<Void, StoreNewUserProfilePictureCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;
    private final UserProfilePictureDimensionValidator userProfilePictureDimensionValidator;

    public StoreNewUserProfilePictureUseCase(final UserProfilePictureRepository userProfilePictureRepository,
                                             final UserProfilePictureDimensionValidator userProfilePictureDimensionValidator) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
        this.userProfilePictureDimensionValidator = Objects.requireNonNull(userProfilePictureDimensionValidator);
    }

    @Override
    public Void execute(final StoreNewUserProfilePictureCommand command) {
        userProfilePictureDimensionValidator.validate(command.picture());
        userProfilePictureRepository.storeNewUserProfilePicture(command);
        return null;
    }

}
