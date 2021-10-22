package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.FeatureState;
import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public final class UserProfilePicturesDTO {

    private final List<UserProfilePictureDTO> userProfilePictures;

    private final FeatureState featureState;

    public UserProfilePicturesDTO(final UserProfilePictures userProfilePictures) {
        this.userProfilePictures = userProfilePictures.userProfilePictures().stream()
                .map(UserProfilePictureDTO::new)
                .collect(Collectors.toList());
        this.featureState = userProfilePictures.featureState();
    }

    public List<UserProfilePictureDTO> getUserProfilePictures() {
        return userProfilePictures;
    }

    public FeatureState getFeatureState() {
        return featureState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfilePicturesDTO)) return false;
        UserProfilePicturesDTO that = (UserProfilePicturesDTO) o;
        return Objects.equals(userProfilePictures, that.userProfilePictures) &&
                featureState == that.featureState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfilePictures, featureState);
    }
}
