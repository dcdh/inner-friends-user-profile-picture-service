package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;

import java.util.Objects;

public class StoreNewUserProfilePictureUseCase implements UseCase<Void, StoreNewUserProfilePictureCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;

    public StoreNewUserProfilePictureUseCase(final UserProfilePictureRepository userProfilePictureRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
    }

    @Override
    public Void execute(final StoreNewUserProfilePictureCommand command) {
        userProfilePictureRepository.storeNewUserProfilePicture(command);
        return null;
    }

}
