package com.innerfriends.userprofilepicture.domain;

import java.util.List;

public interface UserProfilePictureRepository {

    UserProfilePictureSaved storeNewUserProfilePicture(NewUserProfilePicture newUserProfilePicture) throws UserProfilePictureRepositoryException;

    ContentUserProfilePicture getContent(UserProfilePictureIdentifier userProfilePictureIdentifier) throws UserProfilePictureUnknownException, UserProfilePictureRepositoryException;

    List<? extends UserProfilePicture> listByUserPseudoAndMediaType(UserPseudo userPseudo, SupportedMediaType mediaType) throws UserProfilePictureRepositoryException;

}
