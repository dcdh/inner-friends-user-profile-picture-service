package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.ContentUserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.usecase.GetContentUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.GetContentUserProfilePictureUseCase;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedGetContentUserProfilePictureUseCase implements UseCase<ContentUserProfilePicture, GetContentUserProfilePictureCommand> {

    private final GetContentUserProfilePictureUseCase getContentUserProfilePictureUseCase;

    public ManagedGetContentUserProfilePictureUseCase(final GetContentUserProfilePictureUseCase getContentUserProfilePictureUseCase) {
        this.getContentUserProfilePictureUseCase = Objects.requireNonNull(getContentUserProfilePictureUseCase);
    }

    @Override
    public ContentUserProfilePicture execute(final GetContentUserProfilePictureCommand command) {
        // TODO cache
        return getContentUserProfilePictureUseCase.execute(command);
    }

}
