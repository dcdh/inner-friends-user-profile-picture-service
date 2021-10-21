package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.UserProfilPictureFeaturedRepositoryException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserProfilPictureFeaturedRepositoryExceptionMapper implements ExceptionMapper<UserProfilPictureFeaturedRepositoryException> {

    @Override
    public Response toResponse(final UserProfilPictureFeaturedRepositoryException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
