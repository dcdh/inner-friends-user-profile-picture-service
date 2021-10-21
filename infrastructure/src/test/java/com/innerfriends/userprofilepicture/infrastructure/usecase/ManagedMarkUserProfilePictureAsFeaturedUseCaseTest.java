package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedCommand;
import com.innerfriends.userprofilepicture.domain.usecase.MarkUserProfilePictureAsFeaturedUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedMarkUserProfilePictureAsFeaturedUseCaseTest {

    @Inject
    ManagedMarkUserProfilePictureAsFeaturedUseCase managedMarkUserProfilePictureAsFeaturedUseCase;

    @InjectMock
    MarkUserProfilePictureAsFeaturedUseCase markUserProfilePictureAsFeaturedUseCase;

    @Test
    public void should_call_decorated() {
        // Given
        final MarkUserProfilePictureAsFeaturedCommand markUserProfilePictureAsFeaturedCommand = mock(MarkUserProfilePictureAsFeaturedCommand.class);
        final UserProfilePictureIdentifier userProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        doReturn(new MarkUserProfilePictureAsFeaturedUseCase.DefaultUserProfilePicture(userProfilePictureIdentifier)).when(markUserProfilePictureAsFeaturedUseCase).execute(markUserProfilePictureAsFeaturedCommand);

        // When && Then
        assertThat(managedMarkUserProfilePictureAsFeaturedUseCase.execute(markUserProfilePictureAsFeaturedCommand)).isEqualTo(new MarkUserProfilePictureAsFeaturedUseCase.DefaultUserProfilePicture(userProfilePictureIdentifier));
    }

}
