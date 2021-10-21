package com.innerfriends.userprofilepicture.domain;

import java.util.Optional;

public interface UserProfilPictureFeaturedRepository {

    Optional<UserProfilePictureIdentifier> getFeatured(UserPseudo userPseudo) throws UserProfilPictureFeaturedRepositoryException;

    UserProfilePictureIdentifier markAsFeatured(UserProfilePictureIdentifier userProfilePictureIdentifier)
            throws UserProfilPictureFeaturedRepositoryException;

}
