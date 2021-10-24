package com.innerfriends.userprofilepicture.infrastructure.arangodb;

import com.arangodb.entity.BaseDocument;
import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.domain.VersionId;

import java.util.Objects;

public final class ArangoDBProfilePictureIdentifier implements UserProfilePictureIdentifier {

    public String userPseudo;

    public SupportedMediaType mediaType;

    public String versionId;

    public ArangoDBProfilePictureIdentifier(final BaseDocument baseDocument) {
        this.userPseudo = baseDocument.getKey();
        this.mediaType = SupportedMediaType.valueOf((String) baseDocument.getAttribute("mediaType"));
        this.versionId = (String) baseDocument.getAttribute("versionId");
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
        if (!(o instanceof ArangoDBProfilePictureIdentifier)) return false;
        ArangoDBProfilePictureIdentifier that = (ArangoDBProfilePictureIdentifier) o;
        return Objects.equals(userPseudo, that.userPseudo) &&
                mediaType == that.mediaType &&
                Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPseudo, mediaType, versionId);
    }
}
