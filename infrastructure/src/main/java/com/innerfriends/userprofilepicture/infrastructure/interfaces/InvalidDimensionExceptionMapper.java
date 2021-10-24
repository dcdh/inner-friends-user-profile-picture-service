package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.InvalidDimensionException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidDimensionExceptionMapper implements ExceptionMapper<InvalidDimensionException> {

    @Override
    public Response toResponse(final InvalidDimensionException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .entity(String.format("Invalid Dimension - current height %d, current width %d - max height %d, max width %d",
                        exception.getCurrentDimension().height().getValue(),
                        exception.getCurrentDimension().width().getValue(),
                        exception.getMaxDimension().height().getValue(),
                        exception.getMaxDimension().width().getValue()))
                .build();
    }

}
