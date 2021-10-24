package com.innerfriends.userprofilepicture.infrastructure.usecase.cache;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.UserPseudo;

public interface CachedUserProfilePictures extends UserProfilePictures {

    UserPseudo userPseudo();

    UserProfilePictureIdentifier featured();

    default boolean hasFeaturedInCache() {
        return featured() != null;
    }

    default boolean hasUserProfilePictures() {
        return userProfilePictures() != null;
    }

}
