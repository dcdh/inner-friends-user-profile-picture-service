package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.usecase.CreateDamdamdeoProfilePictureCommand;
import io.quarkus.runtime.StartupEvent;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Objects;

@ApplicationScoped
public class CreateDamdamdeoProfilePictureUseCaseExecutor {

    private final ManagedCreateDamdamdeoProfilePictureUseCase managedCreateDamdamdeoProfilePictureUseCase;

    public CreateDamdamdeoProfilePictureUseCaseExecutor(final ManagedCreateDamdamdeoProfilePictureUseCase managedCreateDamdamdeoProfilePictureUseCase) {
        this.managedCreateDamdamdeoProfilePictureUseCase = Objects.requireNonNull(managedCreateDamdamdeoProfilePictureUseCase);
    }


    public void onStartup(@Observes @Priority(10) final StartupEvent startupEvent) {
        managedCreateDamdamdeoProfilePictureUseCase.execute(new CreateDamdamdeoProfilePictureCommand());
    }

}
