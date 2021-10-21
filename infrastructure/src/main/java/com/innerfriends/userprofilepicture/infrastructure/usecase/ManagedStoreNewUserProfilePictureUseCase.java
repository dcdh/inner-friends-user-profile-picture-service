package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.SingleInstanceUseCaseExecution;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedStoreNewUserProfilePictureUseCase implements UseCase<Void, StoreNewUserProfilePictureCommand> {

    private final StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase;

    public ManagedStoreNewUserProfilePictureUseCase(final StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase) {
        this.storeNewUserProfilePictureUseCase = Objects.requireNonNull(storeNewUserProfilePictureUseCase);
    }

    @SingleInstanceUseCaseExecution
    @Override
    public Void execute(final StoreNewUserProfilePictureCommand command) {
        // TODO cache
        // TODO @SingleInstanceExecution
        this.storeNewUserProfilePictureUseCase.execute(command);
        return null;
    }

}
