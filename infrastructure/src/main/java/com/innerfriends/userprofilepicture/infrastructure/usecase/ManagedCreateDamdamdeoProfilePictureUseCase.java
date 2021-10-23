package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UseCase;
import com.innerfriends.userprofilepicture.domain.usecase.CreateDamdamdeoProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.CreateDamdamdeoProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.SingleInstanceUseCaseExecution;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedCreateDamdamdeoProfilePictureUseCase implements UseCase<Void, CreateDamdamdeoProfilePictureCommand> {

    private final CreateDamdamdeoProfilePictureUseCase createDamdamdeoProfilePictureUseCase;

    public ManagedCreateDamdamdeoProfilePictureUseCase(final CreateDamdamdeoProfilePictureUseCase createDamdamdeoProfilePictureUseCase) {
        this.createDamdamdeoProfilePictureUseCase = Objects.requireNonNull(createDamdamdeoProfilePictureUseCase);
    }

    @SingleInstanceUseCaseExecution
    @Override
    public Void execute(final CreateDamdamdeoProfilePictureCommand command) {
        return createDamdamdeoProfilePictureUseCase.execute(command);
    }

}
