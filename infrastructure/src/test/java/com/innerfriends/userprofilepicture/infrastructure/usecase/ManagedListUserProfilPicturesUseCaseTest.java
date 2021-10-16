package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesCommand;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedListUserProfilPicturesUseCaseTest {

    @Inject
    ManagedListUserProfilPicturesUseCase managedListUserProfilPicturesUseCase;

    @InjectMock
    ListUserProfilPicturesUseCase listUserProfilPicturesUseCase;

    @Test
    public void should_call_decorated() {
        // Given
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = mock(ListUserProfilPicturesCommand.class);
        final List<? extends UserProfilePicture> userProfilePictures = Collections.emptyList();
        doReturn(userProfilePictures).when(listUserProfilPicturesUseCase).execute(listUserProfilPicturesCommand);

        // When && Then
        assertThat(managedListUserProfilPicturesUseCase.execute(listUserProfilPicturesCommand)).isEqualTo(userProfilePictures);
    }

}
