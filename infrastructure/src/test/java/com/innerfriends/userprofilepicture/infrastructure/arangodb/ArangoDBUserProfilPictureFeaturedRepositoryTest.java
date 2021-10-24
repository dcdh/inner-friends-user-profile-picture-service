package com.innerfriends.userprofilepicture.infrastructure.arangodb;

import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.innerfriends.userprofilepicture.domain.*;
import com.innerfriends.userprofilepicture.infrastructure.opentelemetry.OpenTelemetryTracingService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
public class ArangoDBUserProfilPictureFeaturedRepositoryTest {

    public static final List<String> DELETE_ALL_DOCUMENTS = Arrays.asList("FOR f IN FEATURE REMOVE f IN FEATURE");

    @Inject
    ArangoDBUserProfilPictureFeaturedRepository arangoDBUserProfilPictureFeaturedRepository;

    @Inject
    ArangoDB arangoDB;

    @ConfigProperty(name = "arangodb.dbName")
    String dbName;

    @InjectMock
    OpenTelemetryTracingService openTelemetryTracingService;

    @BeforeEach
    @AfterEach
    public void flush() {
        DELETE_ALL_DOCUMENTS
                .stream()
                .forEach(query -> arangoDB.db(dbName).query(query, Void.class));
    }

    @Test
    public void should_mark_user_profile_picture_has_featured_when_not_done_previously() {
        // Given

        // When
        final UserProfilePictureIdentifier userProfilePictureIdentifier = arangoDBUserProfilPictureFeaturedRepository.markAsFeatured(new UserProfilePictureIdentifier() {

            @Override
            public UserPseudo userPseudo() {
                return () -> "pseudo";
            }

            @Override
            public SupportedMediaType mediaType() {
                return SupportedMediaType.IMAGE_JPEG;
            }

            @Override
            public VersionId versionId() {
                return () -> "v0";
            }

        });

        // Then
        final BaseDocument featured = arangoDB.db(dbName).collection(ArangoDBUserProfilPictureFeaturedRepository.COLLECTION_FEATURE)
                .getDocument("pseudo", BaseDocument.class);
        assertThat(featured).isNotNull();
        assertThat(featured.getKey()).isEqualTo("pseudo");
        assertThat(featured.getAttribute("mediaType")).isEqualTo("IMAGE_JPEG");
        assertThat(featured.getAttribute("versionId")).isEqualTo("v0");

        assertThat(userProfilePictureIdentifier.userPseudo().pseudo()).isEqualTo("pseudo");
        assertThat(userProfilePictureIdentifier.mediaType()).isEqualTo(SupportedMediaType.IMAGE_JPEG);
        assertThat(userProfilePictureIdentifier.versionId().version()).isEqualTo("v0");
        verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    @Test
    public void should_mark_user_profile_picture_has_featured_when_done_previously() {
        // Given
        final String query = "INSERT { _key: \"pseudo\", mediaType : \"IMAGE_JPEG\" , versionId: \"v0\" } INTO FEATURE";
        arangoDB.db(dbName).query(query, Void.class);

        // When
        final UserProfilePictureIdentifier userProfilePictureIdentifier = arangoDBUserProfilPictureFeaturedRepository.markAsFeatured(new UserProfilePictureIdentifier() {

            @Override
            public UserPseudo userPseudo() {
                return () -> "pseudo";
            }

            @Override
            public SupportedMediaType mediaType() {
                return SupportedMediaType.IMAGE_JPEG;
            }

            @Override
            public VersionId versionId() {
                return () -> "v1";
            }

        });

        // Then
        final BaseDocument featured = arangoDB.db(dbName).collection(ArangoDBUserProfilPictureFeaturedRepository.COLLECTION_FEATURE)
                .getDocument("pseudo", BaseDocument.class);
        assertThat(featured).isNotNull();
        assertThat(featured.getKey()).isEqualTo("pseudo");
        assertThat(featured.getAttribute("mediaType")).isEqualTo("IMAGE_JPEG");
        assertThat(featured.getAttribute("versionId")).isEqualTo("v1");

        assertThat(userProfilePictureIdentifier.userPseudo().pseudo()).isEqualTo("pseudo");
        assertThat(userProfilePictureIdentifier.mediaType()).isEqualTo(SupportedMediaType.IMAGE_JPEG);
        assertThat(userProfilePictureIdentifier.versionId().version()).isEqualTo("v1");
    }

    @Test
    public void should_return_featured_user_profile_picture() {
        // Given
        final String query = "INSERT { _key: \"pseudo\", mediaType : \"IMAGE_JPEG\" , versionId: \"v0\" } INTO FEATURE";
        arangoDB.db(dbName).query(query, Void.class);

        // When
        final Optional<UserProfilePictureIdentifier> userProfilePictureIdentifier = arangoDBUserProfilPictureFeaturedRepository.getFeatured(() -> "pseudo");

        // Then
        assertThat(userProfilePictureIdentifier.isPresent()).isTrue();
        assertThat(userProfilePictureIdentifier.get().userPseudo().pseudo()).isEqualTo("pseudo");
        assertThat(userProfilePictureIdentifier.get().mediaType()).isEqualTo(SupportedMediaType.IMAGE_JPEG);
        assertThat(userProfilePictureIdentifier.get().versionId().version()).isEqualTo("v0");
        verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    @Test
    public void should_return_empty_optional_user_profile_picture_identifier_when_user_profile_picture_has_not_be_featured() {
        // Given

        // When
        final Optional<UserProfilePictureIdentifier> userProfilePictureIdentifier = arangoDBUserProfilPictureFeaturedRepository.getFeatured(() -> "pseudo");

        // Then
        assertThat(userProfilePictureIdentifier.isPresent()).isFalse();
    }

    @Test
    public void should_return_featured_user_profile_picture_fail_with_user_profil_picture_featured_repository_exception_when_user_pseudo_is_invalid() {
        // Given

        // When && Then
        assertThatThrownBy(() -> arangoDBUserProfilPictureFeaturedRepository.getFeatured(() -> ""))
                .isInstanceOf(UserProfilPictureFeaturedRepositoryException.class);
    }

}
