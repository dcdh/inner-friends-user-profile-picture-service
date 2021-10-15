package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureSaved;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureUseCase;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedStoreNewUserProfilePictureUseCase implements UseCase<UserProfilePictureSaved, StoreNewUserProfilePictureCommand> {

    private final StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase;

    public ManagedStoreNewUserProfilePictureUseCase(final StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase) {
        this.storeNewUserProfilePictureUseCase = Objects.requireNonNull(storeNewUserProfilePictureUseCase);
    }

    @Override
    public UserProfilePictureSaved execute(final StoreNewUserProfilePictureCommand command) {
        // TODO cache
        // TODO @SingleInstanceExecution
        return this.storeNewUserProfilePictureUseCase.execute(command);
    }

}
