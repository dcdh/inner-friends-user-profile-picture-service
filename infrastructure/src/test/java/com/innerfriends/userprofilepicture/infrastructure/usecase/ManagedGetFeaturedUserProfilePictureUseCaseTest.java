package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureUseCase;
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
public class ManagedGetFeaturedUserProfilePictureUseCaseTest {

    @Inject
    ManagedGetFeaturedUserProfilePictureUseCase managedGetFeaturedUserProfilePictureUseCase;

    @InjectMock
    GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCase;

    @InjectMock
    LockMechanism lockMechanism;

    @Test
    public void should_call_decorated() {
        // Given
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = mock(GetFeaturedUserProfilePictureCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(getFeaturedUserProfilePictureCommand).userPseudo();
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        doReturn(userProfilePicture).when(getFeaturedUserProfilePictureUseCase).execute(getFeaturedUserProfilePictureCommand);
        final InOrder inOrder = inOrder(getFeaturedUserProfilePictureUseCase, lockMechanism);

        // When && Then
        assertThat(managedGetFeaturedUserProfilePictureUseCase.execute(getFeaturedUserProfilePictureCommand)).isEqualTo(userProfilePicture);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(getFeaturedUserProfilePictureUseCase, times(1)).execute(getFeaturedUserProfilePictureCommand);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

}
