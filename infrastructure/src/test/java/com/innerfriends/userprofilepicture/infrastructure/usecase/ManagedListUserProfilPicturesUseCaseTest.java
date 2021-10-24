package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesCommand;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.CachedUserProfilePictures;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.UserProfilePicturesCacheRepository;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.LockMechanism;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.Optional;

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

    @InjectMock
    UserProfilePicturesCacheRepository userProfilePicturesCacheRepository;

    @Test
    public void should_call_decorated_when_not_in_cache() {
        // Given
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = mock(ListUserProfilPicturesCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(listUserProfilPicturesCommand).userPseudo();
        final UserProfilePictures userProfilePictures = mock(UserProfilePictures.class);
        doReturn(Optional.empty()).when(userProfilePicturesCacheRepository).get(userPseudo);
        doReturn(userProfilePictures).when(listUserProfilPicturesUseCase).execute(listUserProfilPicturesCommand);
        final InOrder inOrder = inOrder(listUserProfilPicturesUseCase, lockMechanism, userProfilePicturesCacheRepository);

        // When && Then
        assertThat(managedListUserProfilPicturesUseCase.execute(listUserProfilPicturesCommand)).isEqualTo(userProfilePictures);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).get(userPseudo);
        inOrder.verify(listUserProfilPicturesUseCase, times(1)).execute(listUserProfilPicturesCommand);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).store(userPseudo, userProfilePictures);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

    @Test
    public void should_call_decorated_when_cache_has_not_identifiers_in_cache() {
        // Given
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = mock(ListUserProfilPicturesCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(listUserProfilPicturesCommand).userPseudo();
        final UserProfilePictures userProfilePictures = mock(UserProfilePictures.class);
        final CachedUserProfilePictures cachedUserProfilePictures = mock(CachedUserProfilePictures.class);
        doReturn(false).when(cachedUserProfilePictures).hasUserProfilePictures();
        doReturn(Optional.of(cachedUserProfilePictures)).when(userProfilePicturesCacheRepository).get(userPseudo);
        doReturn(userProfilePictures).when(listUserProfilPicturesUseCase).execute(listUserProfilPicturesCommand);
        final InOrder inOrder = inOrder(listUserProfilPicturesUseCase, lockMechanism, userProfilePicturesCacheRepository);

        // When && Then
        assertThat(managedListUserProfilPicturesUseCase.execute(listUserProfilPicturesCommand)).isEqualTo(userProfilePictures);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).get(userPseudo);
        inOrder.verify(listUserProfilPicturesUseCase, times(1)).execute(listUserProfilPicturesCommand);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).store(userPseudo, userProfilePictures);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

    @Test
    public void should_not_call_decorated_when_in_cache() {
        // Given
        final ListUserProfilPicturesCommand listUserProfilPicturesCommand = mock(ListUserProfilPicturesCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(listUserProfilPicturesCommand).userPseudo();
        final CachedUserProfilePictures cachedUserProfilePictures = mock(CachedUserProfilePictures.class);
        doReturn(true).when(cachedUserProfilePictures).hasUserProfilePictures();
        doReturn(Optional.of(cachedUserProfilePictures)).when(userProfilePicturesCacheRepository).get(userPseudo);
        final InOrder inOrder = inOrder(listUserProfilPicturesUseCase, lockMechanism, userProfilePicturesCacheRepository);

        // When && Then
        assertThat(managedListUserProfilPicturesUseCase.execute(listUserProfilPicturesCommand)).isEqualTo(cachedUserProfilePictures);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).get(userPseudo);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
        verify(listUserProfilPicturesUseCase, never()).execute(any());
    }

}