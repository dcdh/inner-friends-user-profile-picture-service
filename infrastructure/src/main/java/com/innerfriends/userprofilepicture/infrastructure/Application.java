package com.innerfriends.userprofilepicture.infrastructure;

import com.innerfriends.userprofilepicture.domain.UserProfilPictureFeaturedRepository;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureDimensionValidator;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepository;
import com.innerfriends.userprofilepicture.domain.usecase.*;
import com.innerfriends.userprofilepicture.infrastructure.s3.S3ObjectKey;
import com.innerfriends.userprofilepicture.infrastructure.s3.S3ObjectKeyProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class Application {

    @Produces
    @ApplicationScoped
    public StoreNewUserProfilePictureUseCase storeNewUserProfilePictureUseCaseProducer(final UserProfilePictureRepository userProfilePictureRepository,
                                                                                       final UserProfilePictureDimensionValidator userProfilePictureDimensionValidator) {
        return new StoreNewUserProfilePictureUseCase(userProfilePictureRepository, userProfilePictureDimensionValidator);
    }

    @Produces
    @ApplicationScoped
    public GetContentUserProfilePictureUseCase getContentUserProfilePictureUseCaseProducer(final UserProfilePictureRepository userProfilePictureRepository) {
        return new GetContentUserProfilePictureUseCase(userProfilePictureRepository);
    }

    @Produces
    @ApplicationScoped
    public ListUserProfilPicturesUseCase listUserProfilPicturesUseCaseProducer(final UserProfilePictureRepository userProfilePictureRepository,
                                                                               final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository) {
        return new ListUserProfilPicturesUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository);
    }

    @Produces
    @ApplicationScoped
    public MarkUserProfilePictureAsFeaturedUseCase markUserProfilePictureAsFeaturedUseCaseProducer(final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository) {
        return new MarkUserProfilePictureAsFeaturedUseCase(userProfilPictureFeaturedRepository);
    }

    @Produces
    @ApplicationScoped
    public GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCaseProducer(final UserProfilePictureRepository userProfilePictureRepository,
                                                                                             final UserProfilPictureFeaturedRepository userProfilPictureFeaturedRepository) {
        return new GetFeaturedUserProfilePictureUseCase(userProfilePictureRepository, userProfilPictureFeaturedRepository);
    }

    @Produces
    @ApplicationScoped
    public CreateDamdamdeoProfilePictureUseCase createDamdamdeoProfilePictureUseCaseProducer(final UserProfilePictureRepository userProfilePictureRepository) {
        return new CreateDamdamdeoProfilePictureUseCase(userProfilePictureRepository);
    }

    @ApplicationScoped
    @Produces
    public S3ObjectKeyProvider s3ObjectKeyProvider() {
        return (userPseudo, mediaType) -> new S3ObjectKey(userPseudo, mediaType);
    }

}
