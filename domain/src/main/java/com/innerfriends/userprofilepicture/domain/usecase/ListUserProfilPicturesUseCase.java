package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;

import java.util.List;
import java.util.Objects;

public class ListUserProfilPicturesUseCase implements UseCase<List<? extends UserProfilePicture>, ListUserProfilPicturesCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;

    public ListUserProfilPicturesUseCase(final UserProfilePictureRepository userProfilePictureRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
    }

    @Override
    public List<? extends UserProfilePicture> execute(final ListUserProfilPicturesCommand command) {
        return userProfilePictureRepository.listByUserPseudoAndMediaType(command.userPseudo(), command.mediaType());
    }

}
