package com.innerfriends.userprofilepicture.infrastructure.usecase.cache;

import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.UserPseudo;

import java.util.Optional;

public interface UserProfilePicturesCacheRepository {

    Optional<CachedUserProfilePictures> get(UserPseudo userPseudo);

    void store(UserPseudo userPseudo, UserProfilePictures userProfilePictures);

    void storeFeatured(UserProfilePicture featured);

    void evict(UserPseudo userPseudo);

}
