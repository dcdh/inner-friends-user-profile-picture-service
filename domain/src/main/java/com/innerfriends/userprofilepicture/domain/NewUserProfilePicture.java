package com.innerfriends.userprofilepicture.domain;

public interface NewUserProfilePicture {

    UserPseudo userPseudo();

    byte[] picture();

    SupportedMediaType mediaType();

}
