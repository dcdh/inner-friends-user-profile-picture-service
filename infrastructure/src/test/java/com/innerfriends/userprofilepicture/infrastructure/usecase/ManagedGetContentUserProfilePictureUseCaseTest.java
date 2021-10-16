package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.ContentUserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.GetContentUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.GetContentUserProfilePictureUseCase;
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
public class ManagedGetContentUserProfilePictureUseCaseTest {

    @Inject
    ManagedGetContentUserProfilePictureUseCase managedGetContentUserProfilePictureUseCase;

    @InjectMock
    GetContentUserProfilePictureUseCase getContentUserProfilePictureUseCase;

    @Test
    public void should_call_decorated() {
        // Given
        final GetContentUserProfilePictureCommand getContentUserProfilePictureCommand = mock(GetContentUserProfilePictureCommand.class);
        final ContentUserProfilePicture contentUserProfilePicture = mock(ContentUserProfilePicture.class);
        doReturn(contentUserProfilePicture).when(getContentUserProfilePictureUseCase).execute(getContentUserProfilePictureCommand);

        // When && Then
        assertThat(managedGetContentUserProfilePictureUseCase.execute(getContentUserProfilePictureCommand)).isEqualTo(contentUserProfilePicture);
    }

}
