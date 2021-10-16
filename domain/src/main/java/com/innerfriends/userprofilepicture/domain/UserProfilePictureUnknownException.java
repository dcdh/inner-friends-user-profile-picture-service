package com.innerfriends.userprofilepicture.domain;

import java.util.Objects;

public class UserProfilePictureUnknownException extends RuntimeException {

    private final UserProfilePictureIdentifier userProfilePictureIdentifier;

    public UserProfilePictureUnknownException(final UserProfilePictureIdentifier userProfilePictureIdentifier) {
        this.userProfilePictureIdentifier = Objects.requireNonNull(userProfilePictureIdentifier);
    }

    public UserProfilePictureIdentifier profilePictureIdentifier() {
        return userProfilePictureIdentifier;
    }
}
