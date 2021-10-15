package com.innerfriends.userprofilepicture.infrastructure.s3;

import com.innerfriends.userprofilepicture.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class S3UserProfilePictureRepositoryTest {

    @Inject
    S3UserProfilePictureRepository s3UserProfilePictureRepository;

    @InjectSpy
    S3Client s3Client;

    @InjectMock
    S3ObjectKeyProvider s3ObjectKeyProvider;

    @ConfigProperty(name = "bucket.user.profile.picture.name")
    String bucketUserProfilePictureName;

    @BeforeEach
    @AfterEach
    public void flush() {
        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .build()).versions();
        objectVersions.stream().forEach(objectVersion -> {
            System.out.println("Delete s3 object " + objectVersion);
            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucketUserProfilePictureName)
                    .delete(Delete.builder()
                            .objects(ObjectIdentifier.builder()
                                    .key(objectVersion.key())
                                    .versionId(objectVersion.versionId())
                                    .build()).build())
                    .build());
        });
    }

    public static final class TestNewUserProfilePicture implements NewUserProfilePicture {

        private final UserPseudo userPseudo;

        public TestNewUserProfilePicture(final UserPseudo userPseudo) {
            this.userPseudo = userPseudo;
        }

        @Override
        public UserPseudo userPseudo() {
            return userPseudo;
        }

        @Override
        public byte[] picture() {
            return "picture".getBytes();
        }

        @Override
        public SupportedMediaType mediaType() {
            return SupportedMediaType.IMAGE_JPEG;
        }
    }

    @Test
    public void should_store_new_user_profile_picture() {
        // Given
        final UserPseudo userPseudo = () -> "userPseudo";
        doReturn(new S3ObjectKey(userPseudo, SupportedMediaType.IMAGE_JPEG))
                .when(s3ObjectKeyProvider).objectKey(userPseudo, SupportedMediaType.IMAGE_JPEG);

        // When
        final UserProfilePictureSaved userProfilePictureSaved = s3UserProfilePictureRepository.storeNewUserProfilePicture(
                new TestNewUserProfilePicture(userPseudo));

        // Then
        assertThat(userProfilePictureSaved.userPseudo().pseudo()).isEqualTo("userPseudo");
        assertThat(userProfilePictureSaved.versionId()).isNotNull();

        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("userPseudo")
                .build()).versions();
        assertThat(objectVersions.size()).isEqualTo(1);
        assertThat(objectVersions.get(0).versionId()).isEqualTo(userProfilePictureSaved.versionId().version());
        assertThat(objectVersions.get(0).key()).isEqualTo("userPseudo.jpeg");

        verify(s3ObjectKeyProvider, times(1)).objectKey(any(), any());
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    public void should_store_multiple_versions_of_user_profile_picture() {
        // Given
        final UserPseudo userPseudo = () -> "userPseudo";
        doReturn(new S3ObjectKey(userPseudo, SupportedMediaType.IMAGE_JPEG))
                .when(s3ObjectKeyProvider).objectKey(userPseudo, SupportedMediaType.IMAGE_JPEG);
        s3UserProfilePictureRepository.storeNewUserProfilePicture(new TestNewUserProfilePicture(userPseudo));

        // When
        s3UserProfilePictureRepository.storeNewUserProfilePicture(new TestNewUserProfilePicture(userPseudo));

        // Then
        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("userPseudo")
                .build()).versions();
        assertThat(objectVersions.size()).isEqualTo(2);
        verify(s3ObjectKeyProvider, times(2)).objectKey(any(), any());
    }

    private static final class NullObjectKey implements ObjectKey {

        @Override
        public String key() {
            return null;
        }
    }

    @Test
    public void should_store_new_user_profile_picture_return_profile_picture_repository_exception_when_object_key_value_is_invalid() {
        // Given
        final UserPseudo userPseudo = () -> "userPseudo";
        doReturn(new NullObjectKey()).when(s3ObjectKeyProvider).objectKey(userPseudo, SupportedMediaType.IMAGE_JPEG);

        // When && Then
        assertThatThrownBy(() -> s3UserProfilePictureRepository.storeNewUserProfilePicture(new TestNewUserProfilePicture(userPseudo)))
                .isInstanceOf(UserProfilePictureRepositoryException.class);

        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("userPseudo")
                .build()).versions();
        assertThat(objectVersions.size()).isEqualTo(0);

        verify(s3ObjectKeyProvider, times(1)).objectKey(any(), any());
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

}
