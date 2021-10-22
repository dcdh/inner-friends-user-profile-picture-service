package com.innerfriends.userprofilepicture.domain;

import java.util.List;

public interface UserProfilePictures {

    FeatureState featureState();

    List<? extends UserProfilePicture> userProfilePictures();

}
