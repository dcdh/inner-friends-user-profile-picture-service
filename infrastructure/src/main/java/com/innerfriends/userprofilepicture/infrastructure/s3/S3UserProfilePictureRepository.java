package com.innerfriends.userprofilepicture.infrastructure.s3;

import com.innerfriends.userprofilepicture.domain.NewUserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepositoryException;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureSaved;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

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

    // TODO span interceptor
    @Override
    public UserProfilePictureSaved storeNewUserProfilePicture(final NewUserProfilePicture newUserProfilePicture) throws UserProfilePictureRepositoryException {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketUserProfilePictureName)
                .key(s3ObjectKeyProvider.objectKey(newUserProfilePicture.userPseudo(),
                        newUserProfilePicture.mediaType()).key())
                .contentType(newUserProfilePicture.mediaType().contentType())
                .build();
        try {
            final PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(newUserProfilePicture.picture()));
            return new S3UserProfilePictureSaved(newUserProfilePicture.userPseudo(),
                    newUserProfilePicture.mediaType(),
                    putObjectResponse);
        } catch (final SdkException sdkException) {
            logger.error("Unable to store new user profile picture for user " + newUserProfilePicture.userPseudo(), sdkException);
            throw new UserProfilePictureRepositoryException();
        }
    }

}
