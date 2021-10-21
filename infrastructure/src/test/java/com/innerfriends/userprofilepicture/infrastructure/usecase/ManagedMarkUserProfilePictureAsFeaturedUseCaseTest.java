package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedCommand;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedUseCase;
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
public class ManagedMarkUserProfilePictureAsFeaturedUseCaseTest {

    @Inject
    ManagedMarkUserProfilePictureAsFeaturedUseCase managedMarkUserProfilePictureAsFeaturedUseCase;

    @InjectMock
    MarkUserProfilePictureAsFeaturedUseCase markUserProfilePictureAsFeaturedUseCase;

    @InjectMock
    LockMechanism lockMechanism;

    @Test
    public void should_call_decorated() {
        // Given
        final MarkUserProfilePictureAsFeaturedCommand markUserProfilePictureAsFeaturedCommand = mock(MarkUserProfilePictureAsFeaturedCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(markUserProfilePictureAsFeaturedCommand).userPseudo();
        final UserProfilePictureIdentifier userProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        doReturn(new MarkUserProfilePictureAsFeaturedUseCase.DefaultUserProfilePicture(userProfilePictureIdentifier)).when(markUserProfilePictureAsFeaturedUseCase).execute(markUserProfilePictureAsFeaturedCommand);
        final InOrder inOrder = inOrder(markUserProfilePictureAsFeaturedUseCase, lockMechanism);

        // When && Then
        assertThat(managedMarkUserProfilePictureAsFeaturedUseCase.execute(markUserProfilePictureAsFeaturedCommand)).isEqualTo(new MarkUserProfilePictureAsFeaturedUseCase.DefaultUserProfilePicture(userProfilePictureIdentifier));
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(markUserProfilePictureAsFeaturedUseCase, times(1)).execute(markUserProfilePictureAsFeaturedCommand);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

}
