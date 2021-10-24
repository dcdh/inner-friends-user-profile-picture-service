package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.innerfriends.userprofilepicture.domain.FeatureState;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.CachedUserProfilePictures;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Objects;

@RegisterForReflection
public final class HazelcastCachedUserProfilePictures implements CachedUserProfilePictures {

    public String userPseudo;
    public List<HazelcastUserProfilePicture> userProfilePictures;
    public HazelcastUserProfilePictureIdentifier featuredUserProfilePictureIdentifier;
    public FeatureState featureState;

    public HazelcastCachedUserProfilePictures() {}

    private HazelcastCachedUserProfilePictures(final Builder builder) {
        this.userPseudo = builder.userPseudo;
        this.userProfilePictures = builder.userProfilePictures;
        this.featuredUserProfilePictureIdentifier = builder.featuredUserProfilePictureIdentifier;
        this.featureState = builder.featureState;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        public String userPseudo;
        public List<HazelcastUserProfilePicture> userProfilePictures;
        public HazelcastUserProfilePictureIdentifier featuredUserProfilePictureIdentifier;
        public FeatureState featureState;

        private Builder() {}

        public Builder withUserPseudo(final String userPseudo) {
            this.userPseudo = userPseudo;
            return this;
        }

        public Builder withUserProfilePictures(final List<HazelcastUserProfilePicture> userProfilePictures) {
            this.userProfilePictures = userProfilePictures;
            return this;
        }

        public Builder withFeaturedUserProfilePictureIdentifier(final HazelcastUserProfilePictureIdentifier featuredUserProfilePictureIdentifier) {
            this.featuredUserProfilePictureIdentifier = featuredUserProfilePictureIdentifier;
            return this;
        }

        public Builder withFeatureState(final FeatureState featureState) {
            this.featureState = featureState;
            return this;
        }

        public HazelcastCachedUserProfilePictures build() {
            return new HazelcastCachedUserProfilePictures(this);
        }
    }

    public HazelcastCachedUserProfilePictures replaceUserProfilePictures(final List<HazelcastUserProfilePicture> userProfilePictures) {
        this.userProfilePictures = userProfilePictures;
        return this;
    }

    public HazelcastCachedUserProfilePictures replaceFeaturedUserProfilePictureIdentifier(final HazelcastUserProfilePictureIdentifier featuredUserProfilePictureIdentifier) {
        this.featuredUserProfilePictureIdentifier = featuredUserProfilePictureIdentifier;
        return this;
    }

    public HazelcastCachedUserProfilePictures replaceFeatureState(final FeatureState featureState) {
        this.featureState = featureState;
        return this;
    }

    @Override
    public FeatureState featureState() {
        return featureState;
    }

    @Override
    public List<? extends UserProfilePicture> userProfilePictures() {
        return userProfilePictures;
    }

    @Override
    public UserPseudo userPseudo() {
        return () -> userPseudo;
    }

    @Override
    public UserProfilePictureIdentifier featured() {
        return featuredUserProfilePictureIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HazelcastCachedUserProfilePictures)) return false;
        HazelcastCachedUserProfilePictures that = (HazelcastCachedUserProfilePictures) o;
        return Objects.equals(userPseudo, that.userPseudo) &&
                Objects.equals(userProfilePictures, that.userProfilePictures) &&
                Objects.equals(featuredUserProfilePictureIdentifier, that.featuredUserProfilePictureIdentifier) &&
                featureState == that.featureState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPseudo, userProfilePictures, featuredUserProfilePictureIdentifier, featureState);
    }

    @Override
    public String toString() {
        return "HazelcastCachedUserProfilePictures{" +
                "userPseudo='" + userPseudo + '\'' +
                ", userProfilePictures=" + userProfilePictures +
                ", featuredUserProfilePictureIdentifier=" + featuredUserProfilePictureIdentifier +
                ", featureState=" + featureState +
                '}';
    }
}
