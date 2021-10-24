package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesCommand;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.CachedUserProfilePictures;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.UserProfilePicturesCacheRepository;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.SingleInstanceUseCaseExecution;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedListUserProfilPicturesUseCase implements UseCase<UserProfilePictures, ListUserProfilPicturesCommand> {

    private final ListUserProfilPicturesUseCase listUserProfilPicturesUseCase;
    private final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository;

    public ManagedListUserProfilPicturesUseCase(final ListUserProfilPicturesUseCase listUserProfilPicturesUseCase,
                                                final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository) {
        this.listUserProfilPicturesUseCase = Objects.requireNonNull(listUserProfilPicturesUseCase);
        this.userProfilePicturesCacheRepository = Objects.requireNonNull(userProfilePicturesCacheRepository);
    }

    @SingleInstanceUseCaseExecution
    @Override
    public UserProfilePictures execute(final ListUserProfilPicturesCommand command) {
        return userProfilePicturesCacheRepository.get(command.userPseudo())
                .filter(CachedUserProfilePictures::hasUserProfilePictures)
                .map(UserProfilePictures.class::cast)
                .orElseGet(() -> {
                    final UserProfilePictures userProfilePictures = listUserProfilPicturesUseCase.execute(command);
                    userProfilePicturesCacheRepository.store(command.userPseudo(), userProfilePictures);
                    return userProfilePictures;
                });
    }

}
