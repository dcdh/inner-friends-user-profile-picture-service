package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.*;
import com.innerfriends.userprofilepicture.domain.usecase.StoreNewUserProfilePictureCommand;
import com.innerfriends.userprofilepicture.infrastructure.usecase.ManagedStoreNewUserProfilePictureUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class UserProfilePictureEndpointTest {

    @InjectMock
    ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase;

    public static class TestUserProfilePictureSaved implements UserProfilePictureSaved {

        @Override
        public UserPseudo userPseudo() {
            return () -> "userPseudo";
        }

        @Override
        public SupportedMediaType mediaType() {
            return SupportedMediaType.IMAGE_JPEG;
        }

        @Override
        public VersionId versionId() {
            return () -> "v0";
        }

    }

    @Test
    public void should_upload_user_profile_picture() throws Exception {
        // Given
        doReturn(new TestUserProfilePictureSaved())
                .when(managedStoreNewUserProfilePictureUseCase).execute(
                        new StoreNewUserProfilePictureCommand(
                                new JaxRsUserPseudo("userPseudo"),
                                Files.readAllBytes(getFileFromResource("given/1px_white.jpg").toPath()),
                                SupportedMediaType.IMAGE_JPEG));

        // When && Then
        given()
                .multiPart("picture", getFileFromResource("given/1px_white.jpg"))
                .multiPart("supportedMediaType", "IMAGE_JPEG")
                .when()
                .post("/users/userPseudo/storeNewUserProfilePicture")
                .then()
                .log().all()
                .statusCode(201)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/profilePicture.json"))
                .body("userPseudo", equalTo("userPseudo"))
                .body("mediaType", equalTo("IMAGE_JPEG"))
                .body("versionId", equalTo("v0"));
        verify(managedStoreNewUserProfilePictureUseCase, times(1)).execute(any());
    }

    @Test
    public void should_upload_user_profile_picture_return_expected_response_when_profile_picture_repository_exception_is_thrown() throws Exception {
        // Given
        doThrow(new UserProfilePictureRepositoryException()).when(managedStoreNewUserProfilePictureUseCase).execute(any());

        // When && Then
        given()
                .multiPart("picture", getFileFromResource("given/1px_white.jpg"))
                .multiPart("supportedMediaType", "IMAGE_JPEG")
                .when()
                .post("/users/userPseudo/storeNewUserProfilePicture")
                .then()
                .log().all()
                .statusCode(500);
        verify(managedStoreNewUserProfilePictureUseCase, times(1)).execute(any());
    }

    private File getFileFromResource(final String fileName) throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        return new File(resource.toURI());
    }

}
