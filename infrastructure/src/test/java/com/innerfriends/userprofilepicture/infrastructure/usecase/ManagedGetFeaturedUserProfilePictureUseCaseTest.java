package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureUseCase;
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
public class ManagedGetFeaturedUserProfilePictureUseCaseTest {

    @Inject
    ManagedGetFeaturedUserProfilePictureUseCase managedGetFeaturedUserProfilePictureUseCase;

    @InjectMock
    GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCase;

    @InjectMock
    LockMechanism lockMechanism;

    @InjectMock
    UserProfilePicturesCacheRepository userProfilePicturesCacheRepository;

    @Test
    public void should_call_decorated_when_not_in_cache() {
        // Given
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = mock(GetFeaturedUserProfilePictureCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(getFeaturedUserProfilePictureCommand).userPseudo();
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        doReturn(Optional.empty()).when(userProfilePicturesCacheRepository).get(userPseudo);
        doReturn(userProfilePicture).when(getFeaturedUserProfilePictureUseCase).execute(getFeaturedUserProfilePictureCommand);
        final InOrder inOrder = inOrder(getFeaturedUserProfilePictureUseCase, lockMechanism, userProfilePicturesCacheRepository);

        // When && Then
        assertThat(managedGetFeaturedUserProfilePictureUseCase.execute(getFeaturedUserProfilePictureCommand)).isEqualTo(userProfilePicture);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).get(userPseudo);
        inOrder.verify(getFeaturedUserProfilePictureUseCase, times(1)).execute(getFeaturedUserProfilePictureCommand);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).storeFeatured(userPseudo, userProfilePicture);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

    @Test
    public void should_call_decorated_when_cache_has_not_identifiers_in_cache() {
        // Given
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = mock(GetFeaturedUserProfilePictureCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(getFeaturedUserProfilePictureCommand).userPseudo();
        final UserProfilePicture userProfilePicture = mock(UserProfilePicture.class);
        final CachedUserProfilePictures cachedUserProfilePictures = mock(CachedUserProfilePictures.class);
        doReturn(false).when(cachedUserProfilePictures).hasFeaturedInCache();
        doReturn(Optional.of(cachedUserProfilePictures)).when(userProfilePicturesCacheRepository).get(userPseudo);
        doReturn(userProfilePicture).when(getFeaturedUserProfilePictureUseCase).execute(getFeaturedUserProfilePictureCommand);
        final InOrder inOrder = inOrder(getFeaturedUserProfilePictureUseCase, lockMechanism, userProfilePicturesCacheRepository);

        // When && Then
        assertThat(managedGetFeaturedUserProfilePictureUseCase.execute(getFeaturedUserProfilePictureCommand)).isEqualTo(userProfilePicture);
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).get(userPseudo);
        inOrder.verify(getFeaturedUserProfilePictureUseCase, times(1)).execute(getFeaturedUserProfilePictureCommand);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).storeFeatured(userPseudo, userProfilePicture);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
    }

    @Test
    public void should_not_call_decorated_when_in_cache() {
        // Given
        final GetFeaturedUserProfilePictureCommand getFeaturedUserProfilePictureCommand = mock(GetFeaturedUserProfilePictureCommand.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(userPseudo).when(getFeaturedUserProfilePictureCommand).userPseudo();
        final CachedUserProfilePictures cachedUserProfilePictures = mock(CachedUserProfilePictures.class);
        doReturn(true).when(cachedUserProfilePictures).hasFeaturedInCache();
        final UserProfilePictureIdentifier userProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        doReturn(userProfilePictureIdentifier).when(cachedUserProfilePictures).featured();
        doReturn(Optional.of(cachedUserProfilePictures)).when(userProfilePicturesCacheRepository).get(userPseudo);
        final InOrder inOrder = inOrder(getFeaturedUserProfilePictureUseCase, lockMechanism, userProfilePicturesCacheRepository);

        // When && Then
        assertThat(managedGetFeaturedUserProfilePictureUseCase.execute(getFeaturedUserProfilePictureCommand))
                .isEqualTo(new ManagedGetFeaturedUserProfilePictureUseCase.FeaturedUserProfilePicture(userProfilePictureIdentifier));
        inOrder.verify(lockMechanism, times(1)).lock(userPseudo);
        inOrder.verify(userProfilePicturesCacheRepository, times(1)).get(userPseudo);
        inOrder.verify(lockMechanism, times(1)).release(userPseudo);
        verify(getFeaturedUserProfilePictureUseCase, never()).execute(any());
    }

}