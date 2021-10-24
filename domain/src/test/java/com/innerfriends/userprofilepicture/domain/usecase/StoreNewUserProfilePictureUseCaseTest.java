package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureDimensionValidator;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreNewUserProfilePictureUseCaseTest {

    @Test
    public void should_store_new_user_profile_picture() {
        // Given
        final UserProfilePictureRepository userProfilePictureRepository = mock(UserProfilePictureRepository.class);
        final StoreNewUserProfilePictureCommand storeNewUserProfilePictureCommand = mock(StoreNewUserProfilePictureCommand.class);
        doReturn("picture".getBytes()).when(storeNewUserProfilePictureCommand).picture();
        final UserProfilePictureDimensionValidator userProfilePictureDimensionValidator = mock(UserProfilePictureDimensionValidator.class);
        final InOrder inOrder = inOrder(userProfilePictureRepository, userProfilePictureDimensionValidator);

        // When && Then
        assertThat(new StoreNewUserProfilePictureUseCase(userProfilePictureRepository, userProfilePictureDimensionValidator)
                .execute(storeNewUserProfilePictureCommand)).isEqualTo(null);
        inOrder.verify(userProfilePictureDimensionValidator, times(1)).validate("picture".getBytes());
        inOrder.verify(userProfilePictureRepository, times(1)).storeNewUserProfilePicture(storeNewUserProfilePictureCommand);
    }

}
