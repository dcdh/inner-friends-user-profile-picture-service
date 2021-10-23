package com.innerfriends.userprofilepicture.infrastructure.s3;

import com.innerfriends.userprofilepicture.domain.*;
import com.innerfriends.userprofilepicture.infrastructure.opentelemetry.NewSpan;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class S3UserProfilePictureRepository implements UserProfilePictureRepository {

    private final Logger logger = LoggerFactory.getLogger(S3UserProfilePictureRepository.class);

    private final S3Client s3Client;
    private final String bucketUserProfilePictureName;
    private final S3ObjectKeyProvider s3ObjectKeyProvider;

    public S3UserProfilePictureRepository(final S3Client s3Client,
                                          @ConfigProperty(name = "bucket.user.profile.picture.name") final String bucketUserProfilePictureName,
                                          final S3ObjectKeyProvider s3ObjectKeyProvider) {
        this.s3Client = Objects.requireNonNull(s3Client);
        this.bucketUserProfilePictureName = Objects.requireNonNull(bucketUserProfilePictureName);
        this.s3ObjectKeyProvider = Objects.requireNonNull(s3ObjectKeyProvider);
    }

    @NewSpan
    @Override
    public UserProfilePictureIdentifier storeNewUserProfilePicture(final NewUserProfilePicture newUserProfilePicture) throws UserProfilePictureRepositoryException {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketUserProfilePictureName)
                .key(s3ObjectKeyProvider.objectKey(newUserProfilePicture.userPseudo(),
                        newUserProfilePicture.mediaType()).key())
                .contentType(newUserProfilePicture.mediaType().contentType())
                .build();
        try {
            final PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(newUserProfilePicture.picture()));
            return new S3UserProfilePictureIdentifier(newUserProfilePicture.userPseudo(),
                    newUserProfilePicture.mediaType(),
                    putObjectResponse);
        } catch (final SdkException sdkException) {
            logger.error("Unable to store new user profile picture for user " + newUserProfilePicture.userPseudo(), sdkException);
            throw new UserProfilePictureRepositoryException();
        }
    }

    @NewSpan
    @Override
    public ContentUserProfilePicture getContent(final UserProfilePictureIdentifier userProfilePictureIdentifier)
            throws UserProfilePictureUnknownException, UserProfilePictureRepositoryException {
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketUserProfilePictureName)
                .key(s3ObjectKeyProvider.objectKey(userProfilePictureIdentifier.userPseudo(), userProfilePictureIdentifier.mediaType()).key())
                .versionId(userProfilePictureIdentifier.versionId().version())
                .build();
        try {
            final ResponseBytes<GetObjectResponse> getObjectResponse = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());
            return new S3ContentUserProfilePicture(userProfilePictureIdentifier.userPseudo(), getObjectResponse);
        } catch (final NoSuchKeyException noSuchKeyException) {
            throw  new UserProfilePictureUnknownException(userProfilePictureIdentifier);
        } catch (final SdkException sdkException) {
            if (sdkException.getMessage().startsWith("Invalid version id specified")) {
                throw  new UserProfilePictureUnknownException(userProfilePictureIdentifier);
            }
            throw new UserProfilePictureRepositoryException();
        }
    }

    @NewSpan
    @Override
    public List<? extends UserProfilePictureIdentifier> listByUserPseudoAndMediaType(final UserPseudo userPseudo, final SupportedMediaType mediaType)
            throws UserProfilePictureRepositoryException {
        final ListObjectVersionsRequest listObjectVersionsRequest = ListObjectVersionsRequest.builder()
                .bucket(bucketUserProfilePictureName)
                .prefix(s3ObjectKeyProvider.objectKey(userPseudo, mediaType).key())
                .build();
        final ListObjectVersionsResponse listObjectVersionsResponse = s3Client.listObjectVersions(listObjectVersionsRequest);
        return listObjectVersionsResponse.versions()
                .stream()
                .map(objectVersion -> new S3UserProfilePictureIdentifier(userPseudo, mediaType, objectVersion))
                .collect(Collectors.toList());
    }

    @NewSpan
    @Override
    public Optional<UserProfilePictureIdentifier> getFirst(final UserPseudo userPseudo, final SupportedMediaType mediaType) throws UserProfilePictureRepositoryException {
        final ListObjectVersionsRequest listObjectVersionsRequest = ListObjectVersionsRequest.builder()
                .bucket(bucketUserProfilePictureName)
                .prefix(s3ObjectKeyProvider.objectKey(userPseudo, mediaType).key())
                .build();
        final ListObjectVersionsResponse listObjectVersionsResponse = s3Client.listObjectVersions(listObjectVersionsRequest);
        final List<ObjectVersion> objectVersions = listObjectVersionsResponse.versions();
        if (objectVersions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new S3UserProfilePictureIdentifier(userPseudo, mediaType,
                objectVersions.get(objectVersions.size() - 1)));
    }

}
