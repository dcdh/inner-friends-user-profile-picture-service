package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedCommand;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedUseCase;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedMarkUserProfilePictureAsFeaturedUseCase implements UseCase<UserProfilePicture, MarkUserProfilePictureAsFeaturedCommand> {

    private final MarkUserProfilePictureAsFeaturedUseCase markUserProfilePictureAsFeaturedUseCase;

    public ManagedMarkUserProfilePictureAsFeaturedUseCase(final MarkUserProfilePictureAsFeaturedUseCase markUserProfilePictureAsFeaturedUseCase) {
        this.markUserProfilePictureAsFeaturedUseCase = Objects.requireNonNull(markUserProfilePictureAsFeaturedUseCase);
    }

    @Override
    public UserProfilePicture execute(final MarkUserProfilePictureAsFeaturedCommand command) {
        // TODO cache
        return this.markUserProfilePictureAsFeaturedUseCase.execute(command);
    }

}
