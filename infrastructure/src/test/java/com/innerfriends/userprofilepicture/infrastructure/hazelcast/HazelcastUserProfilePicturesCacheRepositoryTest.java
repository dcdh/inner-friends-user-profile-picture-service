package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.innerfriends.userprofilepicture.domain.*;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.CachedUserProfilePictures;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @BeforeEach
    @AfterEach
    public void flush() {
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).clear();
    }

    @Test
    public void should_get_return_stored_cached_user_profile_picture() throws Exception {
        // Given
        final CachedUserProfilePictures givenCachedUserProfilePictures = HazelcastCachedUserProfilePictures.newBuilder()
                .setUserPseudo("user")
                .setFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenCachedUserProfilePictures));

        // When
        final Optional<CachedUserProfilePictures> cachedUserProfilePictures = hazelcastUserProfilePicturesCacheRepository.get(() -> "user");

        // Then
        assertThat(cachedUserProfilePictures.isPresent()).isTrue();
        assertThat(cachedUserProfilePictures.get()).isEqualTo(HazelcastCachedUserProfilePictures.newBuilder()
                .setUserPseudo("user")
                .setFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
                .build());
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
                .setUserPseudo("user")
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenCachedUserProfilePictures));

        // When
        hazelcastUserProfilePicturesCacheRepository.storeFeatured(() -> "user",
                HazelcastUserProfilePictureIdentifier.newBuilder()
                        .setUserPseudo("user")
                        .setMediaType(SupportedMediaType.IMAGE_JPEG)
                        .setVersionId("v0").build());

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .setUserPseudo("user")
                .setFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
                .build();

        assertThat(objectMapper.readValue(
                (String) hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user"), HazelcastCachedUserProfilePictures.class))
                .isEqualTo(expectedHazelcastCachedUserProfilePicture);
    }

    @Test
    public void should_store_featured_profile_picture_when_user_profile_picture_not_in_cache() throws Exception {
        // Given

        // When
        hazelcastUserProfilePicturesCacheRepository.storeFeatured(() -> "user",
                HazelcastUserProfilePictureIdentifier.newBuilder()
                        .setUserPseudo("user")
                        .setMediaType(SupportedMediaType.IMAGE_JPEG)
                        .setVersionId("v0").build());

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .setUserPseudo("user")
                .setFeaturedUserProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
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

        // When
        hazelcastUserProfilePicturesCacheRepository.store(() -> "user", userProfilePictures);

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .setUserPseudo("user")
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v1").build())
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v2").build())
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
        final HazelcastCachedUserProfilePictures givenHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .setUserPseudo("user")
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v1").build())
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v2").build())
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenHazelcastCachedUserProfilePicture));

        // When
        hazelcastUserProfilePicturesCacheRepository.store(() -> "user", userProfilePictures);

        // Then
        final HazelcastCachedUserProfilePictures expectedHazelcastCachedUserProfilePicture = HazelcastCachedUserProfilePictures.newBuilder()
                .setUserPseudo("user")
                .addProfilePictureIdentifier(
                        HazelcastUserProfilePictureIdentifier.newBuilder()
                                .setUserPseudo("user")
                                .setMediaType(SupportedMediaType.IMAGE_JPEG)
                                .setVersionId("v0").build())
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
                .setUserPseudo("user")
                .build();
        hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).put("user",
                objectMapper.writeValueAsString(givenCachedUserProfilePictures));

        // When
        hazelcastUserProfilePicturesCacheRepository.evict(() -> "user");

        // Then
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("user")).isNull();
    }

    private List<UserProfilePicture> buildUserProfilePicture(final int nbOfPictures) {
        return IntStream.range(0, nbOfPictures)
                .boxed()
                .map(index -> new TestUserProfilePictureIdentifier("v" + index))
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

        public TestUserProfilePictureIdentifier(final String versionId) {
            this.versionId = new TestVersionId(versionId);
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
            return false;
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
