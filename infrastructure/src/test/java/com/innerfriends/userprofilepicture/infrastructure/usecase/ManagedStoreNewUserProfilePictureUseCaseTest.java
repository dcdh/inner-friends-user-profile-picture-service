package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureSaved;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ManagedStoreNewUserProfilePictureUseCaseTest {

    @Inject
    ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase;

    @InjectMock
    StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCase;

    @Test
    public void should_call_decorated() {
        // Given
        final StoreNewUserProfilePictureCommand storeNewUserProfilePictureCommand = mock(StoreNewUserProfilePictureCommand.class);
        final UserProfilePictureSaved userProfilePictureSaved = mock(UserProfilePictureSaved.class);
        doReturn(userProfilePictureSaved).when(storeNewUserProfilePictureUseCase).execute(storeNewUserProfilePictureCommand);

        // When && Then
        assertThat(managedStoreNewUserProfilePictureUseCase.execute(storeNewUserProfilePictureCommand)).isEqualTo(userProfilePictureSaved);
        verify(storeNewUserProfilePictureUseCase, times(1)).execute(any());
    }

}
