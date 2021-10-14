package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureSaved;

import java.util.Objects;

public class StoreNewUserProfilePictureUseCase implements UseCase<UserProfilePictureSaved, StoreNewUserProfilePictureCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;

    public StoreNewUserProfilePictureUseCase(final UserProfilePictureRepository userProfilePictureRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
    }

    @Override
    public UserProfilePictureSaved execute(final StoreNewUserProfilePictureCommand command) {
        return userProfilePictureRepository.storeNewUserProfilePicture(command);
    }

}
