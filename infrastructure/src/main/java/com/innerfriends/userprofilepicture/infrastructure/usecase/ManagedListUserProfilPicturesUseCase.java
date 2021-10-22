package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesCommand;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesUseCase;
import com.innerfriends.userprofilepicture.infrastructure.SingleInstanceUseCaseExecution;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedListUserProfilPicturesUseCase implements UseCase<UserProfilePictures, ListUserProfilPicturesCommand> {

    private final ListUserProfilPicturesUseCase listUserProfilPicturesUseCase;

    public ManagedListUserProfilPicturesUseCase(final ListUserProfilPicturesUseCase listUserProfilPicturesUseCase) {
        this.listUserProfilPicturesUseCase = Objects.requireNonNull(listUserProfilPicturesUseCase);
    }

    @SingleInstanceUseCaseExecution
    @Override
    public UserProfilePictures execute(final ListUserProfilPicturesCommand command) {
        // TODO cache
        return listUserProfilPicturesUseCase.execute(command);
    }

}
