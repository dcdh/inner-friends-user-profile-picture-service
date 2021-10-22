package com.innerfriends.userprofilepicture.infrastructure.usecase.cache;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.UserPseudo;

import java.util.Optional;

public interface UserProfilePicturesCacheRepository {

    Optional<CachedUserProfilePictures> get(UserPseudo userPseudo);

    void store(UserPseudo userPseudo, UserProfilePictures userProfilePictures);

    void storeFeatured(UserPseudo userPseudo, UserProfilePictureIdentifier featured);

    void evict(UserPseudo userPseudo);

}
