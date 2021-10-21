package com.innerfriends.userprofilepicture.domain;

import java.util.List;

public interface UserProfilePictureRepository {

    UserProfilePictureIdentifier storeNewUserProfilePicture(NewUserProfilePicture newUserProfilePicture) throws UserProfilePictureRepositoryException;

    ContentUserProfilePicture getContent(UserProfilePictureIdentifier userProfilePictureIdentifier) throws UserProfilePictureUnknownException, UserProfilePictureRepositoryException;

    List<? extends UserProfilePictureIdentifier> listByUserPseudoAndMediaType(UserPseudo userPseudo, SupportedMediaType mediaType) throws UserProfilePictureRepositoryException;

}
