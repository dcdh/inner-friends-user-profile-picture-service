package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.infrastructure.usecase.ManagedStoreNewUserProfilePictureUseCase;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/users")
public class UserProfilePictureEndpoint {

    private final ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase;

    public UserProfilePictureEndpoint(final ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase) {
        this.managedStoreNewUserProfilePictureUseCase = Objects.requireNonNull(managedStoreNewUserProfilePictureUseCase);
    }

    @POST
    @Path("/{userPseudo}/storeNewUserProfilePicture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response storeNewUserProfilePicture(@PathParam("userPseudo") final String userPseudo,
                                               @MultipartForm final UserProfilePictureToStore userProfilePictureToStore) {
        final UserProfilePicture userProfilePicture = managedStoreNewUserProfilePictureUseCase.execute(
                new StoreNewUserProfilePictureCommand(
                        new JaxRsUserPseudo(userPseudo), userProfilePictureToStore.picture, userProfilePictureToStore.mediaType));
        return Response
                .status(Response.Status.CREATED)
                .entity(new UserProfilePictureDTO(userProfilePicture))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
