package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.NewUserProfilePicture;
import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UseCaseCommand;
import com.innerfriends.userprofilepicture.domain.UserPseudo;

import java.util.Arrays;
import java.util.Objects;

public final class StoreNewUserProfilePictureCommand implements UseCaseCommand, NewUserProfilePicture {

    private final UserPseudo userPseudo;

    private final byte[] picture;

    private final SupportedMediaType mediaType;

    public StoreNewUserProfilePictureCommand(final UserPseudo userPseudo, final byte[] picture, final SupportedMediaType mediaType) {
        this.userPseudo = Objects.requireNonNull(userPseudo);
        this.picture = Objects.requireNonNull(picture);
        this.mediaType = Objects.requireNonNull(mediaType);
    }

    @Override
    public UserPseudo userPseudo() {
        return userPseudo;
    }

    @Override
    public byte[] picture() {
        return picture;
    }

    @Override
    public SupportedMediaType mediaType() {
        return mediaType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoreNewUserProfilePictureCommand)) return false;
        StoreNewUserProfilePictureCommand that = (StoreNewUserProfilePictureCommand) o;
        return Objects.equals(userPseudo, that.userPseudo) &&
                Arrays.equals(picture, that.picture) &&
                mediaType == that.mediaType;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(userPseudo, mediaType);
        result = 31 * result + Arrays.hashCode(picture);
        return result;
    }
}
