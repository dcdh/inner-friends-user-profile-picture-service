package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureUnknownException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserProfilePictureUnknownExceptionMapper implements ExceptionMapper<UserProfilePictureUnknownException> {

    @Override
    public Response toResponse(final UserProfilePictureUnknownException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
