package com.innerfriends.userprofilepicture.domain;

public interface UserProfilePictureIdentifier {

    UserPseudo userPseudo();

    SupportedMediaType mediaType();

    VersionId versionId();

    default boolean isEqual(final UserProfilePictureIdentifier other) {
        return other.userPseudo().pseudo().equals(userPseudo().pseudo())
                && other.mediaType().equals(mediaType())
                && other.versionId().version().equals(versionId().version());
    }

}
