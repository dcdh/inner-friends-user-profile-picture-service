package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.UserProfilePictureRepositoryException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserProfilePictureRepositoryExceptionMapper implements ExceptionMapper<UserProfilePictureRepositoryException> {

    @Override
    public Response toResponse(final UserProfilePictureRepositoryException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}
