package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.usecase.CreateDamdamdeoProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.CreateDamdamdeoProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.LockMechanism;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedCreateDamdamdeoProfilePictureUseCaseTest {

    @Inject
    ManagedCreateDamdamdeoProfilePictureUseCase managedCreateDamdamdeoProfilePictureUseCase;

    @InjectMock
    LockMechanism lockMechanism;

    @InjectMock
    CreateDamdamdeoProfilePictureUseCase createDamdamdeoProfilePictureUseCase;

    @Test
    public void should_call_decorated() {
        // Given
        final CreateDamdamdeoProfilePictureCommand createDamdamdeoProfilePictureCommand = mock(CreateDamdamdeoProfilePictureCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(createDamdamdeoProfilePictureCommand).userPseudo();
        final InOrder inOrder = inOrder(createDamdamdeoProfilePictureUseCase, lockMechanism);

        // When
        managedCreateDamdamdeoProfilePictureUseCase.execute(createDamdamdeoProfilePictureCommand);

        // Then
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(createDamdamdeoProfilePictureUseCase, times(1)).execute(createDamdamdeoProfilePictureCommand);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

}
