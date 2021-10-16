package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesCommand;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesUseCase;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ManagedListUserProfilPicturesUseCase implements UseCase<List<? extends UserProfilePicture>, ListUserProfilPicturesCommand> {

    private final ListUserProfilPicturesUseCase listUserProfilPicturesUseCase;

    public ManagedListUserProfilPicturesUseCase(final ListUserProfilPicturesUseCase listUserProfilPicturesUseCase) {
        this.listUserProfilPicturesUseCase = Objects.requireNonNull(listUserProfilPicturesUseCase);
    }

    @Override
    public List<? extends UserProfilePicture> execute(final ListUserProfilPicturesCommand command) {
        // TODO cache
        return listUserProfilPicturesUseCase.execute(command);
    }

}
