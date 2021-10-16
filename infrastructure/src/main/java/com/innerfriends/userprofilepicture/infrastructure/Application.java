package com.innerfriends.userprofilepicture.infrastructure;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import com.innerfriends.userprofilepicture.domain.usecase.GetContentUserProfilePictureUseCase;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.s3.S3ObjectKey;
import com.innerfriends.userprofilepicture.infrastructure.s3.S3ObjectKeyProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class Application {

    @Produces
    @ApplicationScoped
    public StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCaseProducer(final UserProfilePictureRepository userProfilePictureRepository) {
        return new StoreNewUserProfilePictureUseCase(userProfilePictureRepository);
    }

    @Produces
    @ApplicationScoped
    public GetContentUserProfilePictureUseCase getContentUserProfilePictureUseCaseProducer(final UserProfilePictureRepository userProfilePictureRepository) {
        return new GetContentUserProfilePictureUseCase(userProfilePictureRepository);
    }

    @ApplicationScoped
    @Produces
    public S3ObjectKeyProvider s3ObjectKeyProvider() {
        return (userPseudo, mediaType) -> new S3ObjectKey(userPseudo, mediaType);
    }

}
