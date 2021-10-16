package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.ContentUserProfilePicture;
import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.usecase.GetContentUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.domain.usecase.ListUserProfilPicturesCommand;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.infrastructure.usecase.ManagedGetContentUserProfilePictureUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.ManagedListUserProfilPicturesUseCase;
import com.innerfriends.userprofilepicture.infrastructure.usecase.ManagedStoreNewUserProfilePictureUseCase;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

@Path("/users")
public class UserProfilePictureEndpoint {

    private final ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase;
    private final ManagedGetContentUserProfilePictureUseCase managedGetContentUserProfilePictureUseCase;
    private final ManagedListUserProfilPicturesUseCase managedListUserProfilPicturesUseCase;

    public UserProfilePictureEndpoint(final ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase,
                                      final ManagedGetContentUserProfilePictureUseCase managedGetContentUserProfilePictureUseCase,
                                      final ManagedListUserProfilPicturesUseCase managedListUserProfilPicturesUseCase) {
        this.managedStoreNewUserProfilePictureUseCase = Objects.requireNonNull(managedStoreNewUserProfilePictureUseCase);
        this.managedGetContentUserProfilePictureUseCase = Objects.requireNonNull(managedGetContentUserProfilePictureUseCase);
        this.managedListUserProfilPicturesUseCase = Objects.requireNonNull(managedListUserProfilPicturesUseCase);
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

    @GET
    @Consumes("image/jpeg")
    @Path("/{userPseudo}/version/{versionId}/content")
    public Response getJpegContentUserProfilePicture(@PathParam("userPseudo") final String userPseudo,
                                                     @PathParam("versionId") final String versionId) {
        final ContentUserProfilePicture contentUserProfilePicture = managedGetContentUserProfilePictureUseCase.execute(
                new GetContentUserProfilePictureCommand(
                        new JaxRsUserPseudo(userPseudo),
                        SupportedMediaType.IMAGE_JPEG,
                        new JaxRsVersionId(versionId)));
        return Response.ok(contentUserProfilePicture.picture())
                .header("Content-Disposition",
                        String.format("attachment;filename=%s%s", contentUserProfilePicture.userPseudo().pseudo(),
                                contentUserProfilePicture.mediaType().extension()))
                .header("Content-Type", contentUserProfilePicture.mediaType().contentType())
                .header("Content-Length", contentUserProfilePicture.contentLength())
                .header("versionId", contentUserProfilePicture.versionId().version())
                .build();
    }

    @GET
    @Consumes("image/jpeg")
    @Path("/{userPseudo}")
    public Response listUserProfilePictures(@PathParam("userPseudo") final String userPseudo) {
        final List<? extends UserProfilePicture> userProfilePictures = managedListUserProfilPicturesUseCase.execute(
                new ListUserProfilPicturesCommand(
                        new JaxRsUserPseudo(userPseudo),
                        SupportedMediaType.IMAGE_JPEG));
        return Response.ok(new UserProfilePicturesDTO(userProfilePictures))
                .build();
    }

}
