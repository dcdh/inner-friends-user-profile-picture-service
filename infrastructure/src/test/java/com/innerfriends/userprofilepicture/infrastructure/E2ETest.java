package com.innerfriends.userprofilepicture.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class E2ETest {

    @ConfigProperty(name = "bucket.user.profile.picture.name")
    String bucketUserProfilePictureName;

    @Inject
    S3Client s3Client;

    @Test
    @Order(0)
    public void should_store_new_user_profile_picture() throws Exception {
        given()
                .multiPart("picture", getFileFromResource("given/1px_white.jpg"))
                .multiPart("supportedMediaType", "IMAGE_JPEG")
                .when()
                .post("/users/pseudoE2E/storeNewUserProfilePicture")
                .then()
                .log().all()
                .statusCode(201);

        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("pseudoE2E.jpeg")
                .build()).versions();
        assertThat(objectVersions.size()).isEqualTo(1);
    }

    @Test
    @Order(1)
    public void should_download_user_profile_picture_by_version_id() {
        final String versionId = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("pseudoE2E.jpeg")
                .build()).versions().stream().map(ObjectVersion::versionId)
                .findFirst().get();
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/pseudoE2E/version/{versionId}/content", versionId)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    public void should_list_user_profile_pictures() {
        given()
                .header("Content-Type", "image/jpeg")
                .when()
                .get("/users/pseudoE2E")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(3)
    public void should_mark_as_featured() {
        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("pseudoE2E.jpeg")
                .build()).versions();
        given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .post(String.format("/users/pseudoE2E/%s/markAsFeatured", objectVersions.get(0).versionId()))
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    public void should_get_featured_user_profile_picture() throws Exception {
        final List<ObjectVersion> objectVersions = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .prefix("pseudoE2E.jpeg")
                .build()).versions();
        final String versionId = given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/pseudoE2E/featured")
                .then()
                .statusCode(200)
                .extract().path("versionId");
        assertThat(versionId).isEqualTo(objectVersions.get(0).versionId());
    }

    private File getFileFromResource(final String fileName) throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        return new File(resource.toURI());
    }

}
