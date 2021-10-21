package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureUseCase;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedGetFeaturedUserProfilePictureUseCase implements UseCase<UserProfilePicture, GetFeaturedUserProfilePictureCommand> {

    private final GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCase;

    public ManagedGetFeaturedUserProfilePictureUseCase(final GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCase) {
        this.getFeaturedUserProfilePictureUseCase = Objects.requireNonNull(getFeaturedUserProfilePictureUseCase);
    }

    @Override
    public UserProfilePicture execute(final GetFeaturedUserProfilePictureCommand command) {
        // TODO cache
        return getFeaturedUserProfilePictureUseCase.execute(command);
    }

}
