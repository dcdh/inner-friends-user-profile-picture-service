package com.innerfriends.userprofilepicture.infrastructure.interfaces;

import com.innerfriends.userprofilepicture.domain.*;
import com.innerfriends.userprofilepicture.domain.usecase.*;
import com.innerfriends.userprofilepicture.infrastructure.usecase.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class UserProfilePictureEndpointTest {

    @InjectMock
    ManagedStoreNewUserProfilePictureUseCase managedStoreNewUserProfilePictureUseCase;

    @InjectMock
    ManagedGetContentUserProfilePictureUseCase managedGetContentUserProfilePictureUseCase;

    @InjectMock
    ManagedListUserProfilPicturesUseCase managedListUserProfilPicturesUseCase;

    @InjectMock
    ManagedMarkUserProfilePictureAsFeaturedUseCase managedMarkUserProfilePictureAsFeaturedUseCase;

    @InjectMock
    ManagedGetFeaturedUserProfilePictureUseCase managedGetFeaturedUserProfilePictureUseCase;

    @Test
    public void should_store_new_user_profile_picture() throws Exception {
        // Given

        // When && Then
        given()
                .multiPart("picture", getFileFromResource("given/1px_white.jpg"))
                .multiPart("supportedMediaType", "IMAGE_JPEG")
                .when()
                .post("/users/userPseudo/storeNewUserProfilePicture")
                .then()
                .log().all()
                .statusCode(201);

        verify(managedStoreNewUserProfilePictureUseCase, times(1)).execute(
                new StoreNewUserProfilePictureCommand(
                        new JaxRsUserPseudo("userPseudo"),
                        Files.readAllBytes(getFileFromResource("given/1px_white.jpg").toPath()),
                        SupportedMediaType.IMAGE_JPEG));
    }

    @Test
    public void should_store_new_user_profile_picture_return_expected_response_when_user_profile_picture_repository_exception_is_thrown() throws Exception {
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
    }

    public static final class TestContentUserProfilePicture implements ContentUserProfilePicture {

        @Override
        public UserPseudo userPseudo() {
            return () -> "userPseudo";
        }

        @Override
        public byte[] picture() {
            return "picture".getBytes();
        }

        @Override
        public SupportedMediaType mediaType() {
            return SupportedMediaType.IMAGE_JPEG;
        }

        @Override
        public Long contentLength() {
            return 7L;
        }

        @Override
        public VersionId versionId() {
            return () -> "v0";
        }
    }

    @Test
    public void should_get_content_user_profile_picture() {
        // Given
        doReturn(new TestContentUserProfilePicture())
                .when(managedGetContentUserProfilePictureUseCase).execute(
                        new GetContentUserProfilePictureCommand(
                                new JaxRsUserPseudo("userPseudo"),
                                SupportedMediaType.IMAGE_JPEG,
                                new JaxRsVersionId("v0")));

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/userPseudo/version/v0/content")
                .then()
                .log().headers()
                .statusCode(200)
                .header("Content-Disposition", "attachment;filename=userPseudo.jpeg")
                .header("Content-Type","image/jpeg")
                .header("Content-Length","7")
                .header("versionId","v0");
    }

    @Test
    public void should_get_content_user_profile_picture_return_expected_response_when_user_profile_picture_version_unknown_is_thrown() {
        // Given
        doThrow(new UserProfilePictureUnknownException(mock(UserProfilePictureIdentifier.class)))
                .when(managedGetContentUserProfilePictureUseCase).execute(any());

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/userPseudo/version/v0/content")
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    public void should_get_content_user_profile_picture_return_expected_response_when_user_profile_picture_repository_exception_is_thrown() {
        // Given
        doThrow(new UserProfilePictureRepositoryException()).when(managedGetContentUserProfilePictureUseCase).execute(any());

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/userPseudo/version/v0/content")
                .then()
                .log().all()
                .statusCode(500);
    }

    private static final class TestUserProfilePicture implements UserProfilePicture {

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

        @Override
        public boolean isFeatured() {
            return true;
        }
    }

    @Test
    public void should_list_jpeg_user_profile_pictures() {
        // Given
        doReturn(Collections.singletonList(new TestUserProfilePicture())).when(managedListUserProfilPicturesUseCase).execute(
                new ListUserProfilPicturesCommand(
                        new JaxRsUserPseudo("userPseudo"),
                        SupportedMediaType.IMAGE_JPEG));

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/userPseudo")
                .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/profilePictures.json"))
                .body("userProfilePictures[0].userPseudo", equalTo("userPseudo"))
                .body("userProfilePictures[0].mediaType", equalTo("IMAGE_JPEG"))
                .body("userProfilePictures[0].versionId", equalTo("v0"))
                .body("userProfilePictures[0].featured", equalTo(true));
    }

    @Test
    public void should_list_jpeg_user_profile_pictures_return_expected_response_when_user_profile_picture_repository_exception_is_thrown() {
        // Given
        doThrow(new UserProfilePictureRepositoryException()).when(managedListUserProfilPicturesUseCase).execute(
                new ListUserProfilPicturesCommand(
                        new JaxRsUserPseudo("userPseudo"),
                        SupportedMediaType.IMAGE_JPEG));

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/userPseudo")
                .then()
                .log().all()
                .statusCode(500);
    }

    @Test
    public void should_mark_as_featured() {
        // Given
        doReturn(new TestUserProfilePicture()).when(managedMarkUserProfilePictureAsFeaturedUseCase).execute(
                new MarkUserProfilePictureAsFeaturedCommand(
                        new JaxRsUserPseudo("userPseudo"),
                        SupportedMediaType.IMAGE_JPEG,
                        new JaxRsVersionId("v0")));

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .post("/users/userPseudo/v0/markAsFeatured")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/profilePicture.json"))
                .body("userPseudo", equalTo("userPseudo"))
                .body("mediaType", equalTo("IMAGE_JPEG"))
                .body("versionId", equalTo("v0"))
                .body("featured", equalTo(true));
    }

    @Test
    public void should_mark_as_featured_return_expected_response_when_user_profile_picture_featured_repository_exception_is_thrown() {
        // Given
        doThrow(new UserProfilPictureFeaturedRepositoryException()).when(managedMarkUserProfilePictureAsFeaturedUseCase).execute(
                new MarkUserProfilePictureAsFeaturedCommand(
                        new JaxRsUserPseudo("userPseudo"),
                        SupportedMediaType.IMAGE_JPEG,
                        new JaxRsVersionId("v0")));

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .post("/users/userPseudo/v0/markAsFeatured")
                .then()
                .statusCode(500);
    }

    @Test
    public void should_get_featured() {
        // Given
        doReturn(new TestUserProfilePicture()).when(managedGetFeaturedUserProfilePictureUseCase).execute(
                new GetFeaturedUserProfilePictureCommand(
                        new JaxRsUserPseudo("userPseudo"),
                        SupportedMediaType.IMAGE_JPEG));

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/userPseudo/featured")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/profilePicture.json"))
                .body("userPseudo", equalTo("userPseudo"))
                .body("mediaType", equalTo("IMAGE_JPEG"))
                .body("versionId", equalTo("v0"))
                .body("featured", equalTo(true));
    }

    @Test
    public void should_get_featured_return_expected_response_when_user_profile_picture_not_available_yet_is_thrown() {
        // Given
        doThrow(new UserProfilePictureNotAvailableYetException(new JaxRsUserPseudo("userPseudo"))).when(managedGetFeaturedUserProfilePictureUseCase).execute(
                new GetFeaturedUserProfilePictureCommand(
                        new JaxRsUserPseudo("userPseudo"),
                        SupportedMediaType.IMAGE_JPEG));

        // When && Then
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/userPseudo/featured")
                .then()
                .statusCode(404);
    }

    private File getFileFromResource(final String fileName) throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        return new File(resource.toURI());
    }

}
