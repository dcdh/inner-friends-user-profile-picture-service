package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.LockMechanism;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedStoreNewUserProfilePictureUseCaseTest {

    @Inject
    ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase;

    @InjectMock
    StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase;

    @InjectMock
    LockMechanism lockMechanism;

    @Test
    public void should_call_decorated() {
        // Given
        final StoreNewUserProfilePictureCommand storeNewUserProfilePictureCommand = mock(StoreNewUserProfilePictureCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(storeNewUserProfilePictureCommand).userPseudo();
        final InOrder inOrder = inOrder(storeNewUserProfilePictureUseCase, lockMechanism);

        // When && Then
        assertThat(managedStoreNewUserProfilePictureUseCase.execute(storeNewUserProfilePictureCommand)).isEqualTo(null);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(storeNewUserProfilePictureUseCase, times(1)).execute(storeNewUserProfilePictureCommand);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

}
