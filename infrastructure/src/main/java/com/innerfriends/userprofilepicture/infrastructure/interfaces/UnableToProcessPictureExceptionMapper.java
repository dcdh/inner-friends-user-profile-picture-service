package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.UnableToProcessPictureException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnableToProcessPictureExceptionMapper implements ExceptionMapper<UnableToProcessPictureException> {

    @Override
    public Response toResponse(final UnableToProcessPictureException exception) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
