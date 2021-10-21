package com.innerfriends.userprofilepicture.infrastructure.s3;

import com.innerfriends.userprofilepicture.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class S3UserProfilePictureIdentifierRepositoryTest {

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
        reset(s3Client);
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
        final UserProfilePictureIdentifier userProfilePictureIdentifier = s3UserProfilePictureRepository.storeNewUserProfilePicture(
                new TestNewUserProfilePicture(userPseudo));

        // Then
        assertThat(userProfilePictureIdentifier.userPseudo().pseudo()).isEqualTo("userPseudo");
        assertThat(userProfilePictureIdentifier.mediaType()).isEqualTo(SupportedMediaType.IMAGE_JPEG);
        assertThat(userProfilePictureIdentifier.versionId()).isNotNull();

        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("userPseudo")
                .build()).versions();
        assertThat(objectVersions.size()).isEqualTo(1);
        assertThat(objectVersions.get(0).versionId()).isEqualTo(userProfilePictureIdentifier.versionId().version());
        assertThat(objectVersions.get(0).key()).isEqualTo("userPseudo.jpeg");

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

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    private static class TestUserProfilePictureIdentifier implements UserProfilePictureIdentifier {

        private final String versionId;

        public TestUserProfilePictureIdentifier(final String versionId) {
            this.versionId = versionId;
        }

        @Override
        public UserPseudo userPseudo() {
            return () -> "userPseudo";
        }

        @Override
        public SupportedMediaType mediaType() {
            return SupportedMediaType.IMAGE_JPEG;
        }

        @Override
        public VersionId versionId() {
            return () -> versionId;
        }
    }

    @Test
    public void should_get_content() throws Exception {
        // Given
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketUserProfilePictureName)
                .key("userPseudo.jpeg")
                .contentType("image/jpeg")
                .build();
        final String versionId = s3Client.putObject(putObjectRequest, RequestBody.fromBytes("picture".getBytes())).versionId();
        final TestUserProfilePictureIdentifier testProfilePictureIdentifier = new TestUserProfilePictureIdentifier(versionId);
        doReturn(new S3ObjectKey(testProfilePictureIdentifier.userPseudo(), testProfilePictureIdentifier.mediaType()))
                .when(s3ObjectKeyProvider).objectKey(testProfilePictureIdentifier.userPseudo(), testProfilePictureIdentifier.mediaType());

        // When
        final ContentUserProfilePicture contentUserProfilePicture = s3UserProfilePictureRepository.getContent(new TestUserProfilePictureIdentifier(versionId));

        // Then
        assertThat(contentUserProfilePicture.userPseudo().pseudo()).isEqualTo("userPseudo");
        assertThat(contentUserProfilePicture.picture()).isEqualTo("picture".getBytes());
        assertThat(contentUserProfilePicture.mediaType()).isEqualTo(SupportedMediaType.IMAGE_JPEG);
        assertThat(contentUserProfilePicture.contentLength()).isEqualTo(7l);
        assertThat(contentUserProfilePicture.versionId().version()).isEqualTo(versionId);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), any(ResponseTransformer.class));
    }

    @Test
    public void should_get_content_throw_user_profile_picture_unknown_exception_when_user_profile_picture_not_found() {
        // Given
        final UserProfilePictureIdentifier userProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(new S3VersionId("v0")).when(userProfilePictureIdentifier).versionId();
        doReturn(userPseudo).when(userProfilePictureIdentifier).userPseudo();
        doReturn(SupportedMediaType.IMAGE_JPEG).when(userProfilePictureIdentifier).mediaType();
        doReturn(new S3ObjectKey(userPseudo, SupportedMediaType.IMAGE_JPEG))
                .when(s3ObjectKeyProvider).objectKey(userPseudo, SupportedMediaType.IMAGE_JPEG);

        // When && Then
        assertThatThrownBy(() -> s3UserProfilePictureRepository.getContent(userProfilePictureIdentifier))
                .isInstanceOf(UserProfilePictureUnknownException.class);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), any(ResponseTransformer.class));
    }

    @Test
    public void should_get_content_return_profile_picture_repository_exception_when_s3_object_key_is_invalid() {
        // Given
        final UserProfilePictureIdentifier userProfilePictureIdentifier = mock(UserProfilePictureIdentifier.class);
        final UserPseudo userPseudo = mock(UserPseudo.class);
        doReturn(new S3VersionId("v0")).when(userProfilePictureIdentifier).versionId();
        doReturn(userPseudo).when(userProfilePictureIdentifier).userPseudo();
        doReturn(SupportedMediaType.IMAGE_JPEG).when(userProfilePictureIdentifier).mediaType();
        doReturn((ObjectKey) () -> null)
                .when(s3ObjectKeyProvider).objectKey(userPseudo, SupportedMediaType.IMAGE_JPEG);

        // When && Then
        assertThatThrownBy(() -> s3UserProfilePictureRepository.getContent(userProfilePictureIdentifier))
                .isInstanceOf(UserProfilePictureRepositoryException.class);
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), any(ResponseTransformer.class));
    }

    @Test
    public void should_list_by_user_pseudo_and_media_type() throws Exception {
        // Given
        final UserPseudo userPseudo = () -> "user";
        doReturn(new S3ObjectKey(userPseudo, SupportedMediaType.IMAGE_JPEG))
                .when(s3ObjectKeyProvider).objectKey(userPseudo, SupportedMediaType.IMAGE_JPEG);
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketUserProfilePictureName)
                .key("user.jpeg")
                .contentType("image/jpeg")
                .build();
        final String versionId = s3Client.putObject(putObjectRequest, RequestBody.fromBytes("picture".getBytes())).versionId();

        // When
        final List<? extends UserProfilePictureIdentifier> userProfilePicturesIdentifier = s3UserProfilePictureRepository.listByUserPseudoAndMediaType(userPseudo, SupportedMediaType.IMAGE_JPEG);

        // Then
        assertThat(userProfilePicturesIdentifier.size()).isEqualTo(1);
        assertThat(userProfilePicturesIdentifier.get(0).userPseudo().pseudo()).isEqualTo("user");
        assertThat(userProfilePicturesIdentifier.get(0).mediaType()).isEqualTo(SupportedMediaType.IMAGE_JPEG);
        assertThat(userProfilePicturesIdentifier.get(0).versionId().version()).isEqualTo(versionId);
        verify(s3Client, times(1)).listObjectVersions(any(ListObjectVersionsRequest.class));
    }

}
