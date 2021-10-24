package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.VersionId;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public final class HazelcastUserProfilePicture implements UserProfilePicture {

    public String userPseudo;
    public SupportedMediaType mediaType;
    public String versionId;
    public Boolean featured;

    public HazelcastUserProfilePicture() {}

    public HazelcastUserProfilePicture(final Builder builder) {
        this.userPseudo = Objects.requireNonNull(builder.userPseudo);
        this.mediaType = Objects.requireNonNull(builder.mediaType);
        this.versionId = Objects.requireNonNull(builder.versionId);
        this.featured = Objects.requireNonNull(builder.featured);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public boolean isFeatured() {
        return featured;
    }

    @Override
    public UserPseudo userPseudo() {
        return () -> userPseudo;
    }

    @Override
    public SupportedMediaType mediaType() {
        return mediaType;
    }

    @Override
    public VersionId versionId() {
        return () -> versionId;
    }

    public static class Builder {
        public String userPseudo;
        public SupportedMediaType mediaType;
        public String versionId;
        public Boolean featured;

        public Builder withUserPseudo(final String userPseudo) {
            this.userPseudo = userPseudo;
            return this;
        }

        public Builder withMediaType(final SupportedMediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder withVersionId(final String versionId) {
            this.versionId = versionId;
            return this;
        }

        public Builder withFeatured(final Boolean featured) {
            this.featured = featured;
            return this;
        }

        public HazelcastUserProfilePicture build() {
            return new HazelcastUserProfilePicture(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HazelcastUserProfilePicture)) return false;
        HazelcastUserProfilePicture that = (HazelcastUserProfilePicture) o;
        return Objects.equals(userPseudo, that.userPseudo) &&
                mediaType == that.mediaType &&
                Objects.equals(versionId, that.versionId) &&
                Objects.equals(featured, that.featured);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPseudo, mediaType, versionId, featured);
    }

    @Override
    public String toString() {
        return "HazelcastUserProfilePicture{" +
                "userPseudo='" + userPseudo + '\'' +
                ", mediaType=" + mediaType +
                ", versionId='" + versionId + '\'' +
                ", featured=" + featured +
                '}';
    }
}
