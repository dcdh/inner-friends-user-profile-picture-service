package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.VersionId;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public final class HazelcastUserProfilePictureIdentifier implements UserProfilePictureIdentifier {

    public String userPseudo;
    public SupportedMediaType mediaType;
    public String versionId;

    public HazelcastUserProfilePictureIdentifier() {}

    public HazelcastUserProfilePictureIdentifier(final UserProfilePictureIdentifier userProfilePictureIdentifier) {
        this.userPseudo = userProfilePictureIdentifier.userPseudo().pseudo();
        this.mediaType = userProfilePictureIdentifier.mediaType();
        this.versionId = userProfilePictureIdentifier.versionId().version();
    }

    private HazelcastUserProfilePictureIdentifier(final Builder builder) {
        this.userPseudo = builder.userPseudo;
        this.mediaType = builder.mediaType;
        this.versionId = builder.versionId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        public String userPseudo;
        public SupportedMediaType mediaType;
        public String versionId;

        private Builder() {}

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

        public HazelcastUserProfilePictureIdentifier build() {
            return new HazelcastUserProfilePictureIdentifier(this);
        }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HazelcastUserProfilePictureIdentifier)) return false;
        HazelcastUserProfilePictureIdentifier that = (HazelcastUserProfilePictureIdentifier) o;
        return Objects.equals(userPseudo, that.userPseudo) &&
                mediaType == that.mediaType &&
                Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPseudo, mediaType, versionId);
    }

    @Override
    public String toString() {
        return "HazelcastUserProfilePictureIdentifier{" +
                "userPseudo='" + userPseudo + '\'' +
                ", mediaType=" + mediaType +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
