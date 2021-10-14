package com.innerfriends.userprofilepicture.domain;

public interface UserProfilePictureRepository {

    UserProfilePictureSaved storeNewUserProfilePicture(NewUserProfilePicture newUserProfilePicture) throws UserProfilePictureRepositoryException;

}
