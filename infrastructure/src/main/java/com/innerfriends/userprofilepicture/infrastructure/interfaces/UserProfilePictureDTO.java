package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public final class UserProfilePictureDTO {

    private final String userPseudo;
    private final SupportedMediaType mediaType;
    private final String versionId;

    public UserProfilePictureDTO(final UserProfilePicture userProfilePicture) {
        this.userPseudo = userProfilePicture.userPseudo().pseudo();
        this.mediaType = userProfilePicture.mediaType();
        this.versionId = userProfilePicture.versionId().version();
    }

    public String getUserPseudo() {
        return userPseudo;
    }

    public SupportedMediaType getMediaType() {
        return mediaType;
    }

    public String getVersionId() {
        return versionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfilePictureDTO)) return false;
        UserProfilePictureDTO that = (UserProfilePictureDTO) o;
        return Objects.equals(userPseudo, that.userPseudo) &&
                mediaType == that.mediaType &&
                Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPseudo, mediaType, versionId);
    }
}
