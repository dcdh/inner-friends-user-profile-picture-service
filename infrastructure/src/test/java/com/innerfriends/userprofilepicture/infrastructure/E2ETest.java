package com.innerfriends.userprofilepicture.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hazelcast.core.HazelcastInstance;
import com.innerfriends.userprofilepicture.infrastructure.hazelcast.HazelcastUserProfilePicturesCacheRepository;
import com.innerfriends.userprofilepicture.infrastructure.resources.OpenTelemetryLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
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
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class E2ETest {

    @ConfigProperty(name = "bucket.user.profile.picture.name")
    String bucketUserProfilePictureName;

    @Inject
    S3Client s3Client;

    @Inject
    HazelcastInstance hazelcastInstance;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Traces {

        public List<Data> data;

        public List<String> getOperationNames() {
            return data
                    .stream()
                    .flatMap(d -> d.getSpans().stream())
                    .map(Span::getOperationName)
                    .collect(Collectors.toList());
        }

        public List<String> getOperationNamesInError() {
            return data
                    .stream()
                    .flatMap(d -> d.getSpans().stream())
                    .filter(Span::inError)
                    .map(Span::getOperationName)
                    .collect(Collectors.toList());
        }

        public List<Integer> getHttpStatus() {
            return data
                    .stream()
                    .flatMap(d -> d.getSpans().stream())
                    .flatMap(s -> s.httpStatus().stream())
                    .collect(Collectors.toList());
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Data {

        public List<Span> spans;

        public List<Span> getSpans() {
            return spans;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Span {

        public String operationName;
        public List<Tag> tags;

        public String getOperationName() {
            return operationName;
        }

        public boolean inError() {
            return tags.stream().anyMatch(Tag::inError);
        }

        public List<Integer> httpStatus() {
            return tags.stream()
                    .map(t -> t.httpStatus())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Tag {

        public String key;
        public Object value;

        public boolean inError() {
            return "error".equals(key) && Boolean.TRUE.equals(value);
        }

        public Integer httpStatus() {
            if ("http.status_code".equals(key)) {
                return (Integer) value;
            }
            return null;
        }

    }

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
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("pseudoE2E")).isNull();
        final Traces traces = getTraces("/users/pseudoE2E/storeNewUserProfilePicture");
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder("users/{userPseudo}/storeNewUserProfilePicture",
                "HazelcastLockMechanism:lock",
                "HazelcastUserProfilePicturesCacheRepository:evict",
                "S3UserProfilePictureRepository:storeNewUserProfilePicture",
                "HazelcastLockMechanism:release");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(201);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(1)
    public void should_download_user_profile_picture_by_version_id() throws Exception {
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
        final Traces traces = getTraces(String.format("/users/pseudoE2E/version/%s/content", versionId));
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder("users/{userPseudo}/version/{versionId}/content",
                "S3UserProfilePictureRepository:getContent");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(2)
    public void should_list_user_profile_pictures() throws Exception {
        final String firstCallResponse = given()
                .header("Content-Type", "image/jpeg")
                .when()
                .get("/users/pseudoE2E")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response().body().print();
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("pseudoE2E")).isNotNull();
        final Traces firstTraces = getTraces("/users/pseudoE2E");
        assertThat(firstTraces.getOperationNames()).containsExactlyInAnyOrder("users/{userPseudo}",
                "HazelcastLockMechanism:lock",
                "HazelcastUserProfilePicturesCacheRepository:get",
                "S3UserProfilePictureRepository:listByUserPseudoAndMediaType",
                "ArangoDBUserProfilPictureFeaturedRepository:getFeatured",
                "HazelcastUserProfilePicturesCacheRepository:store",
                "HazelcastLockMechanism:release");
        assertThat(firstTraces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(firstTraces.getOperationNamesInError()).isEmpty();
        // second call to check cache issue or not
        final String secondCallResponse = given()
                .header("Content-Type", "image/jpeg")
                .when()
                .get("/users/pseudoE2E")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response().body().print();
        assertThat(secondCallResponse).isEqualTo(firstCallResponse);
    }

    @Test
    @Order(3)
    public void should_mark_as_featured() throws Exception {
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
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("pseudoE2E")).isNull();
        final Traces traces = getTraces(String.format("/users/pseudoE2E/%s/markAsFeatured", objectVersions.get(0).versionId()));
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder("users/{userPseudo}/{versionId}/markAsFeatured",
                "HazelcastLockMechanism:lock",
                "HazelcastUserProfilePicturesCacheRepository:evict",
                "ArangoDBUserProfilPictureFeaturedRepository:markAsFeatured",
                "HazelcastLockMechanism:release");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(4)
    public void should_get_featured_user_profile_picture() throws Exception {
        final String firstCallResponse = given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/pseudoE2E/featured")
                .then()
                .statusCode(200)
                .extract().response().body().print();
        final Traces firstTraces = getTraces("/users/pseudoE2E/featured");
        assertThat(firstTraces.getOperationNames()).containsExactlyInAnyOrder("users/{userPseudo}/featured",
                "HazelcastLockMechanism:lock",
                "HazelcastUserProfilePicturesCacheRepository:get",
                "ArangoDBUserProfilPictureFeaturedRepository:getFeatured",
                "HazelcastUserProfilePicturesCacheRepository:storeFeatured",
                "HazelcastLockMechanism:release");
        assertThat(firstTraces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(firstTraces.getOperationNamesInError()).isEmpty();
        final String secondCallResponse = given()
                .contentType("image/jpeg; charset=ISO-8859-1")
                .when()
                .get("/users/pseudoE2E/featured")
                .then()
                .statusCode(200)
                .extract().response().body().print();
        assertThat(firstCallResponse).isEqualTo(secondCallResponse);

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
        assertThat(hazelcastInstance.getMap(HazelcastUserProfilePicturesCacheRepository.MAP_NAME).get("pseudoE2E")).isNotNull();
    }

    @Test
    @Order(5)
    public void should_be_selected() {
        given()
                .header("Content-Type", "image/jpeg")
                .when()
                .get("/users/pseudoE2E")
                .then()
                .log().all()
                .statusCode(200)
                .body("featureState", equalTo("SELECTED"));
    }

    @Test
    @Order(6)
    public void should_have_stored_Damdamdeo_picture() {
        final List<String> objectVersionsKey = s3Client.listObjectVersions(ListObjectVersionsRequest
                .builder()
                .bucket(bucketUserProfilePictureName)
                .build())
                .versions()
                .stream().map(ObjectVersion::key)
                .collect(Collectors.toList());
        assertThat(objectVersionsKey).contains("Damdamdeo.jpeg");
    }

    private File getFileFromResource(final String fileName) throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        return new File(resource.toURI());
    }

    private Traces getTraces(final String httpTargetValue) throws Exception {
        final Integer hostPort = OpenTelemetryLifecycleManager.getJaegerRestApiHostPort();
        await().atMost(15, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(1l))
                .until(() -> {
                    final Traces traces = given()
                            .when()
                            .queryParam("limit", "1")
                            .queryParam("service", "user-profile-picture")
                            .queryParam("tags", "{\"http.target\":\""+httpTargetValue+"\"}")
                            .get(new URL(String.format("http://localhost:%d/api/traces", hostPort)))
                            .then()
                            .log().all()
                            .contentType(ContentType.JSON)
                            .extract()
                            .body().as(Traces.class);
                    if (traces.getOperationNames().isEmpty()) {
                        return false;
                    }
                    return true;
                });
        return given()
                .when()
                .queryParam("limit", "1")
                .queryParam("service", "user-profile-picture")
                .queryParam("tags", "{\"http.target\":\""+httpTargetValue+"\"}")
                .get(new URL(String.format("http://localhost:%d/api/traces", hostPort)))
                .then()
                .log().all()
                .extract()
                .body().as(Traces.class);
    }

}
