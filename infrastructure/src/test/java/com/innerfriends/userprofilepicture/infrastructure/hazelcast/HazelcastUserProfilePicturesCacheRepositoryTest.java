package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.innerfriends.userprofilepicture.domain.*;
import com.innerfriends.userprofilepicture.infrastructure.opentelemetry.OpenTelemetryTracingService;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.CachedUserProfilePictures;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class HazelcastUserProfilePicturesCacheRepositoryTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    HazelcastUserProfilePicturesCacheRepository hazelcastUserProfilePicturesCacheRepository;

    @Inject
    HazelcastInstance hazelcastInstance;

    @InjectMock
    OpenTelemetryTracingService openTelemetryTracingService;

    @BeforeEach
    @AfterEach
    public void flush() {
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).clear();
    }

    @Test
    public void should_get_return_stored_cached_user_profile_picture() throws Exception {
        // Given
        final CachedUserProfilePictures givenCachedUserProfilePictures = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .withFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .withUserPseudo("user")
                                .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                .withVersionId("v0").build())
                .withUserProfilePictures(
                        Collections.singletonList(
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v0")
                                        .withFeatured(true)
                                        .build()))
                .withFeatureState(FeatureState.SELECTED)
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenCachedUserProfilePictures));

        // When
        final Optional<CachedUserProfilePictures> cachedUserProfilePictures = hazelcastUserProfilePicturesCacheRepository.get(() -> "user");

        // Then
        assertThat(cachedUserProfilePictures.isPresent()).isTrue();
        assertThat(cachedUserProfilePictures.get()).isEqualTo(HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .withFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .withUserPseudo("user")
                                .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                .withVersionId("v0").build())
                .withUserProfilePictures(
                        Collections.singletonList(
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v0")
                                        .withFeatured(true)
                                        .build()))
                .withFeatureState(FeatureState.SELECTED)
                .build());
        verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    @Test
    public void should_return_empty_optional_when_user_profile_picture_not_in_cache() {
        // Given

        // When
        final Optional<CachedUserProfilePictures> cachedUserProfilePictures = hazelcastUserProfilePicturesCacheRepository.get(() -> "user");

        // Then
        assertThat(cachedUserProfilePictures.isPresent()).isFalse();
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user")).isNull();
    }

    @Test
    public void should_store_featured_profile_picture_when_user_profile_picture_in_cache() throws Exception {
        // Given
        final CachedUserProfilePictures givenCachedUserProfilePictures = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenCachedUserProfilePictures));

        // When
        hazelcastUserProfilePicturesCacheRepository.storeFeatured(
                HazelcastUserProfilePicture.newBuilder()
                        .withUserPseudo("user")
                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                        .withVersionId("v0")
                        .withFeatured(true)
                        .build());

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .withFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .withUserPseudo("user")
                                .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                .withVersionId("v0").build())
                .withFeatureState(FeatureState.SELECTED)
                .build();

        assertThat(objectMapper.readValue(
                (String) hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user"), HazelcastCachedUserProfilePictures.class))
                .isEqualTo(expectedHazelcastCachedUserProfilePicture);
        verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    @Test
    public void should_store_featured_profile_picture_when_user_profile_picture_not_in_cache() throws Exception {
        // Given

        // When
        hazelcastUserProfilePicturesCacheRepository.storeFeatured(
                HazelcastUserProfilePicture.newBuilder()
                        .withUserPseudo("user")
                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                        .withVersionId("v0")
                        .withFeatured(true)
                        .build());

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .withFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .withUserPseudo("user")
                                .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                .withVersionId("v0").build())
                .withFeatureState(FeatureState.SELECTED)
                .build();

        assertThat(objectMapper.readValue(
                (String) hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user"), HazelcastCachedUserProfilePictures.class))
                .isEqualTo(expectedHazelcastCachedUserProfilePicture);
    }

    @Test
    public void should_not_store_profile_picture_identifier_when_cannot_be_stored() {
        // Given
        final UserProfilePictures userProfilePictures = mock(UserProfilePictures.class);
        doReturn(false).when(userProfilePictures).canBeStoredInCache();

        // When
        hazelcastUserProfilePicturesCacheRepository.store(() -> "user", userProfilePictures);

        // Then
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user")).isNull();
        verify(userProfilePictures, times(1)).canBeStoredInCache();
    }

    @Test
    public void should_store_profile_picture_identifier_when_user_profile_picture_not_in_cache() throws Exception {
        // Given
        final UserProfilePictures userProfilePictures = mock(UserProfilePictures.class);
        doReturn(true).when(userProfilePictures).canBeStoredInCache();
        doReturn(buildUserProfilePicture(3)).when(userProfilePictures).userProfilePictures();
        doReturn(FeatureState.SELECTED).when(userProfilePictures).featureState();

        // When
        hazelcastUserProfilePicturesCacheRepository.store(() -> "user", userProfilePictures);

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .withUserProfilePictures(
                        Arrays.asList(
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v0")
                                        .withFeatured(true)
                                        .build(),
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v1")
                                        .withFeatured(false)
                                        .build(),
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v2")
                                        .withFeatured(false)
                                        .build()))
                .withFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .withUserPseudo("user")
                                .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                .withVersionId("v0")
                                .build())
                .withFeatureState(FeatureState.SELECTED)
                .build();
        assertThat(objectMapper.readValue(
                (String) hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user"), HazelcastCachedUserProfilePictures.class))
                .isEqualTo(expectedHazelcastCachedUserProfilePicture);
    }

    @Test
    public void should_store_profile_picture_identifier_when_user_profile_picture_in_cache() throws Exception {
        // Given
        final UserProfilePictures userProfilePictures = mock(UserProfilePictures.class);
        doReturn(true).when(userProfilePictures).canBeStoredInCache();
        doReturn(buildUserProfilePicture(1)).when(userProfilePictures).userProfilePictures();
        doReturn(FeatureState.SELECTED).when(userProfilePictures).featureState();
        final HazelcastCachedUserProfilePictures givenHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .withUserProfilePictures(
                        Arrays.asList(
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v0")
                                        .withFeatured(true)
                                        .build(),
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v1")
                                        .withFeatured(false)
                                        .build(),
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v2")
                                        .withFeatured(false)
                                        .build()))
                .withFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .withUserPseudo("user")
                                .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                .withVersionId("v0")
                                .build())
                .withFeatureState(FeatureState.SELECTED)
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenHazelcastCachedUserProfilePicture));

        // When
        hazelcastUserProfilePicturesCacheRepository.store(() -> "user", userProfilePictures);

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .withUserProfilePictures(
                        Collections.singletonList(
                                HazelcastUserProfilePicture.newBuilder()
                                        .withUserPseudo("user")
                                        .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                        .withVersionId("v0")
                                        .withFeatured(true)
                                        .build()))
                .withFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .withUserPseudo("user")
                                .withMediaType(SupportedMediaType.IMAGE_JPEG)
                                .withVersionId("v0")
                                .build())
                .withFeatureState(FeatureState.SELECTED)
                .build();
        assertThat(objectMapper.readValue(
                (String) hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user"), HazelcastCachedUserProfilePictures.class))
                .isEqualTo(expectedHazelcastCachedUserProfilePicture);
    }

    @Test
    public void should_evict_user_profile_pictures() throws Exception {
        // Given
        if (hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user") != null) {
            throw new IllegalStateException("must be null !");
        }
        final CachedUserProfilePictures givenCachedUserProfilePictures = HazelcastCachedUserProfilePictures.newBuilder()
                .withUserPseudo("user")
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenCachedUserProfilePictures));

        // When
        hazelcastUserProfilePicturesCacheRepository.evict(() -> "user");

        // Then
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user")).isNull();
        verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    private List<UserProfilePicture> buildUserProfilePicture(final int nbOfPictures) {
        return IntStream.range(0, nbOfPictures)
                .boxed()
                .map(index -> new TestUserProfilePictureIdentifier("v" + index,
                        index == 0 ? true : false))
                .collect(Collectors.toList());
    }

    private final class TestVersionId implements VersionId {

        private final String versionId;

        public TestVersionId(final String versionId) {
            this.versionId = versionId;
        }

        @Override
        public String version() {
            return versionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestVersionId)) return false;
            TestVersionId that = (TestVersionId) o;
            return Objects.equals(versionId, that.versionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(versionId);
        }
    }

    private final class TestUserProfilePictureIdentifier implements UserProfilePicture {

        private final VersionId versionId;
        private final boolean featured;

        public TestUserProfilePictureIdentifier(final String versionId, final boolean featured) {
            this.versionId = new TestVersionId(versionId);
            this.featured = featured;
        }

        @Override
        public UserPseudo userPseudo() {
            return () -> "user";
        }

        @Override
        public SupportedMediaType mediaType() {
            return SupportedMediaType.IMAGE_JPEG;
        }

        @Override
        public VersionId versionId() {
            return versionId;
        }

        @Override
        public boolean isFeatured() {
            return featured;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestUserProfilePictureIdentifier)) return false;
            TestUserProfilePictureIdentifier that = (TestUserProfilePictureIdentifier) o;
            return Objects.equals(versionId, that.versionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(versionId);
        }

    }

}
