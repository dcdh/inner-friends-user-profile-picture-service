package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedCommand;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.SingleInstanceUseCaseExecution;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.UserProfilePicturesCacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedMarkUserProfilePictureAsFeaturedUseCase implements UseCase<UserProfilePicture, MarkUserProfilePictureAsFeaturedCommand> {

    private final MarkUserProfilePictureAsFeaturedUseCase markUserProfilePictureAsFeaturedUseCase;
    private final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository;

    public ManagedMarkUserProfilePictureAsFeaturedUseCase(final MarkUserProfilePictureAsFeaturedUseCase markUserProfilePictureAsFeaturedUseCase,
                                                          final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository) {
        this.markUserProfilePictureAsFeaturedUseCase = Objects.requireNonNull(markUserProfilePictureAsFeaturedUseCase);
        this.userProfilePicturesCacheRepository = Objects.requireNonNull(userProfilePicturesCacheRepository);
    }

    @SingleInstanceUseCaseExecution
    @Override
    public UserProfilePicture execute(final MarkUserProfilePictureAsFeaturedCommand command) {
        userProfilePicturesCacheRepository.evict(command.userPseudo());
        return this.markUserProfilePictureAsFeaturedUseCase.execute(command);
    }

}
