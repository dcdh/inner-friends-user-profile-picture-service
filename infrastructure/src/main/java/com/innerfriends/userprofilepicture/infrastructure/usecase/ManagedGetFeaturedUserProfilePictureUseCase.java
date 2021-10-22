package com.innerfriends.userprofilepicture.infrastructure.usecase;

import com.innerfriends.userprofilepicture.domain.*;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.GetFeaturedUserProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.CachedUserProfilePictures;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.UserProfilePicturesCacheRepository;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.SingleInstanceUseCaseExecution;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedGetFeaturedUserProfilePictureUseCase implements UseCase<UserProfilePicture, GetFeaturedUserProfilePictureCommand> {

    private final GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCase;
    private final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository;

    public ManagedGetFeaturedUserProfilePictureUseCase(final GetFeaturedUserProfilePictureUseCase getFeaturedUserProfilePictureUseCase,
                                                       final UserProfilePicturesCacheRepository userProfilePicturesCacheRepository) {
        this.getFeaturedUserProfilePictureUseCase = Objects.requireNonNull(getFeaturedUserProfilePictureUseCase);
        this.userProfilePicturesCacheRepository = Objects.requireNonNull(userProfilePicturesCacheRepository);
    }

    @SingleInstanceUseCaseExecution
    @Override
    public UserProfilePicture execute(final GetFeaturedUserProfilePictureCommand command) {
        return userProfilePicturesCacheRepository.get(command.userPseudo())
                .filter(CachedUserProfilePictures::hasFeaturedInCache)
                .map(CachedUserProfilePictures::featured)
                .map(FeaturedUserProfilePicture::new)
                .map(UserProfilePicture.class::cast)
                .orElseGet(() -> {
                    final UserProfilePicture userProfilePicture = getFeaturedUserProfilePictureUseCase.execute(command);
                    userProfilePicturesCacheRepository.storeFeatured(command.userPseudo(), userProfilePicture);
                    return userProfilePicture;
                });
    }

    public static final class FeaturedUserProfilePicture implements UserProfilePicture {

        private final UserProfilePictureIdentifier userProfilePictureIdentifier;

        public FeaturedUserProfilePicture(final UserProfilePictureIdentifier userProfilePictureIdentifier) {
            this.userProfilePictureIdentifier = userProfilePictureIdentifier;
        }

        @Override
        public boolean isFeatured() {
            return true;
        }

        @Override
        public UserPseudo userPseudo() {
            return userProfilePictureIdentifier.userPseudo();
        }

        @Override
        public SupportedMediaType mediaType() {
            return userProfilePictureIdentifier.mediaType();
        }

        @Override
        public VersionId versionId() {
            return userProfilePictureIdentifier.versionId();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FeaturedUserProfilePicture)) return false;
            FeaturedUserProfilePicture that = (FeaturedUserProfilePicture) o;
            return Objects.equals(userProfilePictureIdentifier, that.userProfilePictureIdentifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userProfilePictureIdentifier);
        }
    }
}
