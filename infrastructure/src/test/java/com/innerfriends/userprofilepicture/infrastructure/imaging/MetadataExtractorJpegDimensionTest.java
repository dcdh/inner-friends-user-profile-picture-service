package com.innerfriends.userprofilepicture.infrastructure.imaging;

import com.innerfriends.userprofilepicture.domain.Height;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MetadataExtractorJpegDimensionTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(MetadataExtractorJpegDimension.class).verify();
    }

    @Test
    public void should_return_dimension() throws Exception {
        // Given
        final byte[] picture = this.getClass().getClassLoader().getResourceAsStream("given/640px_400px_white.jpg").readAllBytes();

        // When
        final MetadataExtractorJpegDimension metadataExtractorJpegDimension = new MetadataExtractorJpegDimension(picture);

        // Then
        assertThat(metadataExtractorJpegDimension.height()).isEqualTo(new Height(400));
        assertThat(metadataExtractorJpegDimension.width()).isEqualTo(new Height(640));
    }

}
