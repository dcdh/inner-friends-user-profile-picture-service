package com.innerfriends.userprofilepicture.domain;

public interface UserProfilePictureDimensionValidator {

    void validate(byte[] userProfilePicture) throws InvalidDimensionException, UnableToProcessPictureException;

}
