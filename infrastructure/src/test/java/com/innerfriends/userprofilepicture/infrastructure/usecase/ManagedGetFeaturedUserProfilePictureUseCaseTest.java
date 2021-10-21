package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureUseCase;
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
public class ManagedGetFeaturedUserProfilePictureUseCaseTest {

    @Inject
    ManagedGetFeaturedUserProfilePictureUseCase managedGetFeaturedUserProfilePictureUseCase;

    @InjectMock
    GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCase;

    @Test
    public void should_call_decorated() {
        // Given
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = mock(GetFeaturedUserProfilePictureCommand.class);
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        doReturn(userProfilePicture).when(getFeaturedUserProfilePictureUseCase).execute(getFeaturedUserProfilePictureCommand);

        // When && Then
        assertThat(getFeaturedUserProfilePictureUseCase.execute(getFeaturedUserProfilePictureCommand)).isEqualTo(userProfilePicture);
    }

}
