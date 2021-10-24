package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.UserProfilePicturesCacheRepository;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.SingleInstanceUseCaseExecution;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedStoreNewUserProfilePictureUseCase implements UseCase<Void, StoreNewUserProfilePictureCommand> {

    private final StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase;
    private final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository;

    public ManagedStoreNewUserProfilePictureUseCase(final StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase,
                                                    final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository) {
        this.storeNewUserProfilePictureUseCase = Objects.requireNonNull(storeNewUserProfilePictureUseCase);
        this.userProfilePicturesCacheRepository = Objects.requireNonNull(userProfilePicturesCacheRepository);
    }

    @SingleInstanceUseCaseExecution
    @Override
    public Void execute(final StoreNewUserProfilePictureCommand command) {
        userProfilePicturesCacheRepository.evict(command.userPseudo());
        this.storeNewUserProfilePictureUseCase.execute(command);
        return null;
    }

}
