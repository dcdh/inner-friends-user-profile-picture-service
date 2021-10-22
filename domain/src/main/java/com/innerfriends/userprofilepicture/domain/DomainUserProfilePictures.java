package com.innerfriends.userprofilepicture.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DomainUserProfilePictures implements UserProfilePictures {

    private final List<DomainUserProfilePicture> domainUserProfilePictures;
    private final FeatureState featureState;

    private DomainUserProfilePictures(final Builder builder) {
        this.domainUserProfilePictures = Objects.requireNonNull(builder.domainUserProfilePictures);
        this.featureState = Objects.requireNonNull(builder.featureState);
    }

    public DomainUserProfilePictures(final List<DomainUserProfilePicture> domainUserProfilePictures,
                                     final FeatureState featureState) {
        this.domainUserProfilePictures = Objects.requireNonNull(domainUserProfilePictures);
        this.featureState = Objects.requireNonNull(featureState);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private List<DomainUserProfilePicture> domainUserProfilePictures;
        private FeatureState featureState;

        public Builder withNoPictureToSelect() {
            this.domainUserProfilePictures = Collections.emptyList();
            this.featureState = FeatureState.NO_PICTURE_TO_SELECT;
            return this;
        }

        public Builder withFeaturedStateNotSelectedYet(final List<? extends UserProfilePictureIdentifier> userProfilePictureIdentifiers) {
            final int nbOfUserProfilePictureIdentifiers = userProfilePictureIdentifiers.size();
            this.domainUserProfilePictures = IntStream.range(0, nbOfUserProfilePictureIdentifiers)
                    .mapToObj(index -> {
                        if (index == 0) {
                            return new DomainUserProfilePicture(userProfilePictureIdentifiers.get(index), true);
                        } else {
                            return new DomainUserProfilePicture(userProfilePictureIdentifiers.get(index), false);
                        }
                    })
                    .collect(Collectors.toList());
            this.featureState = FeatureState.NOT_SELECTED_YET_GET_FIRST_ONE;
            return this;
        }

        public Builder withFeaturedStateSelected(final List<? extends UserProfilePictureIdentifier> userProfilePictureIdentifiers,
                                                 final UserProfilePictureIdentifier featured) {
            this.domainUserProfilePictures = userProfilePictureIdentifiers.stream()
                    .map(userProfilePictureIdentifier -> new DomainUserProfilePicture(userProfilePictureIdentifier, featured))
                    .collect(Collectors.toList());
            this.featureState = FeatureState.SELECTED;
            return this;
        }

        public Builder withFeaturedStateInErrorWhenRetrieving(final List<? extends UserProfilePictureIdentifier> userProfilePictureIdentifiers) {
            this.domainUserProfilePictures = userProfilePictureIdentifiers.stream()
                    .map(userProfilePictureIdentifier -> new DomainUserProfilePicture(userProfilePictureIdentifier, false))
                    .collect(Collectors.toList());
            this.featureState = FeatureState.IN_ERROR_WHEN_RETRIEVING;
            return this;
        }

        public DomainUserProfilePictures build() {
            return new DomainUserProfilePictures(this);
        }

    }

    @Override
    public FeatureState featureState() {
        return featureState;
    }

    @Override
    public List<? extends UserProfilePicture> userProfilePictures() {
        return domainUserProfilePictures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainUserProfilePictures)) return false;
        DomainUserProfilePictures that = (DomainUserProfilePictures) o;
        return Objects.equals(domainUserProfilePictures, that.domainUserProfilePictures) &&
                featureState == that.featureState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainUserProfilePictures, featureState);
    }
}
