package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.ContentUserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;

import java.util.Objects;

public class GetContentUserProfilePictureUseCase implements UseCase<ContentUserProfilePicture, GetContentUserProfilePictureCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;

    public GetContentUserProfilePictureUseCase(final UserProfilePictureRepository userProfilePictureRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
    }

    @Override
    public ContentUserProfilePicture execute(final GetContentUserProfilePictureCommand command) {
        return userProfilePictureRepository.getContent(command);
    }
}
