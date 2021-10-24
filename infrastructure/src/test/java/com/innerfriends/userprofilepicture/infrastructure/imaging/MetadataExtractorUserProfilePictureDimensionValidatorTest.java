package com.innerfriends.userprofilepicture.infrastructure.imaging;

import com.innerfriends.userprofilepicture.domain.Height;
import com.innerfriends.userprofilepicture.domain.InvalidDimensionException;
import com.innerfriends.userprofilepicture.domain.Width;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
public class MetadataExtractorUserProfilePictureDimensionValidatorTest {

    @Inject
    MetadataExtractorUserProfilePictureDimensionValidator metadataExtractorUserProfilePictureDimensionValidator;

    // max size defined in application.properties
    @Test
    public void should_validate_dimension() throws Exception {
        // Given
        final byte[] picture = this.getClass().getClassLoader().getResourceAsStream("given/640px_400px_white.jpg").readAllBytes();

        // When && Then
        metadataExtractorUserProfilePictureDimensionValidator.validate(picture);
    }

    @Test
    public void should_throw_invalid_dimension_exception_when_the_size_of_the_picture_exceed_max_size() throws Exception {
        // Given
        final byte[] picture = this.getClass().getClassLoader().getResourceAsStream("given/1280px_1024px_white.jpg").readAllBytes();

        // When && Then
        assertThatThrownBy(() -> metadataExtractorUserProfilePictureDimensionValidator.validate(picture))
                .isInstanceOf(InvalidDimensionException.class)
                .hasFieldOrPropertyWithValue("currentDimension",
                        new MetadataExtractorJpegDimension(new Height(1024), new Width(1280)))
                .hasFieldOrPropertyWithValue("maxDimension",
                        new MaxDimension(new Height(768), new Width(1024)));
    }

}
