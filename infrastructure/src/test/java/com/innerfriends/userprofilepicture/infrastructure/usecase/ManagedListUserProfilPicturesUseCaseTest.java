package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesCommand;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesUseCase;
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
public class ManagedListUserProfilPicturesUseCaseTest {

    @Inject
    ManagedListUserProfilPicturesUseCase managedListUserProfilPicturesUseCase;

    @InjectMock
    ListUserProfilPicturesUseCase listUserProfilPicturesUseCase;

    @InjectMock
    LockMechanism lockMechanism;

    @Test
    public void should_call_decorated() {
        // Given
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = mock(ListUserProfilPicturesCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(listUserProfilPicturesCommand).userPseudo();
        final UserProfilePictures userProfilePictures = mock(UserProfilePictures.class);
        doReturn(userProfilePictures).when(listUserProfilPicturesUseCase).execute(listUserProfilPicturesCommand);
        final InOrder inOrder = inOrder(listUserProfilPicturesUseCase, lockMechanism);

        // When && Then
        assertThat(managedListUserProfilPicturesUseCase.execute(listUserProfilPicturesCommand)).isEqualTo(userProfilePictures);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(listUserProfilPicturesUseCase, times(1)).execute(listUserProfilPicturesCommand);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

}
