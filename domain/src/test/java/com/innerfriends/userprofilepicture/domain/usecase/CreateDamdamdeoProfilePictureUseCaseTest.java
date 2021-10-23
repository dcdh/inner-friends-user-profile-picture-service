package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateDamdamdeoProfilePictureUseCaseTest {

    @Test
    public void should_not_store_damdamdeo_picture_when_already_stored() {
        // Given
        final UserProfilePictureRepository userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        doReturn(Collections.singletonList(mock(UserProfilePictureIdentifier.class)))
                .when(userProfilePictureRepository).listByUserPseudoAndMediaType(new CreateDamdamdeoProfilePictureCommand.DamdamdeoUserPseudo(), SupportedMediaType.IMAGE_JPEG);
        final CreateDamdamdeoProfilePictureUseCase createDamdamdeoProfilePictureUseCase = new CreateDamdamdeoProfilePictureUseCase(userProfilePictureRepository);
        final CreateDamdamdeoProfilePictureCommand createDamdamdeoProfilePictureCommand = new CreateDamdamdeoProfilePictureCommand();

        // When
        createDamdamdeoProfilePictureUseCase.execute(createDamdamdeoProfilePictureCommand);

        // Then
        verify(userProfilePictureRepository, never()).storeNewUserProfilePicture(any());
    }

    @Test
    public void should_store_damdamdeo_picture_when_not_stored_yet() throws Exception {
        // Given
        final UserProfilePictureRepository userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        doReturn(Collections.emptyList())
                .when(userProfilePictureRepository).listByUserPseudoAndMediaType(new CreateDamdamdeoProfilePictureCommand.DamdamdeoUserPseudo(), SupportedMediaType.IMAGE_JPEG);
        final CreateDamdamdeoProfilePictureUseCase createDamdamdeoProfilePictureUseCase = new CreateDamdamdeoProfilePictureUseCase(userProfilePictureRepository);
        final CreateDamdamdeoProfilePictureCommand createDamdamdeoProfilePictureCommand = new CreateDamdamdeoProfilePictureCommand();

        // When
        createDamdamdeoProfilePictureUseCase.execute(createDamdamdeoProfilePictureCommand);

        // Then
        verify(userProfilePictureRepository, times(1)).storeNewUserProfilePicture(
                new CreateDamdamdeoProfilePictureUseCase.DamdamdeoNewUserProfilePicture(
                        createDamdamdeoProfilePictureCommand,
                        this.getClass().getClassLoader().getResourceAsStream("assets/Damdamdeo.jpeg").readAllBytes()));
    }

}
