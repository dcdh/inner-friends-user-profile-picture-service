package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public final class UserProfilePicturesDTO {

    private final List<UserProfilePictureDTO> userProfilePictures;

    public UserProfilePicturesDTO(final List<? extends UserProfilePicture> userProfilePictures) {
        this.userProfilePictures = userProfilePictures.stream()
                .map(UserProfilePictureDTO::new)
                .collect(Collectors.toList());
    }

    public List<UserProfilePictureDTO> getUserProfilePictures() {
        return userProfilePictures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfilePicturesDTO)) return false;
        UserProfilePicturesDTO that = (UserProfilePicturesDTO) o;
        return Objects.equals(userProfilePictures, that.userProfilePictures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfilePictures);
    }
}
