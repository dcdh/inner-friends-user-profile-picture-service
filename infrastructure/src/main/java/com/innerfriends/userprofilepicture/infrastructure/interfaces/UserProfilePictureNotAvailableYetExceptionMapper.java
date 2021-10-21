package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureNotAvailableYetException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserProfilePictureNotAvailableYetExceptionMapper implements ExceptionMapper<UserProfilePictureNotAvailableYetException> {

    @Override
    public Response toResponse(final UserProfilePictureNotAvailableYetException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
