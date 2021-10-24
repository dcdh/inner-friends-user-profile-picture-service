package com.innerfriends.userprofilepicture.infrastructure.imaging;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import com.innerfriends.userprofilepicture.domain.Dimension;
import com.innerfriends.userprofilepicture.domain.Height;
import com.innerfriends.userprofilepicture.domain.Width;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

public final class MetadataExtractorJpegDimension implements Dimension {

    private final Height height;

    private final Width width;

    public MetadataExtractorJpegDimension(final Height height, final Width width) {
        this.height = Objects.requireNonNull(height);
        this.width = Objects.requireNonNull(width);
    }

    public MetadataExtractorJpegDimension(final byte[] picture) throws ImageProcessingException, IOException {
        final Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(picture));
        final JpegDirectory directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        final JpegDescriptor jpegDescriptor = new JpegDescriptor(directory);
        this.height = new Height(extractPixelsFromTagValue(jpegDescriptor, JpegDirectory.TAG_IMAGE_HEIGHT));
        this.width = new Width(extractPixelsFromTagValue(jpegDescriptor, JpegDirectory.TAG_IMAGE_WIDTH));
    }

    private static final Integer extractPixelsFromTagValue(final JpegDescriptor jpegDescriptor, final int tagType) {
        return Integer.parseInt(jpegDescriptor.getDescription(tagType).replace(" pixels", ""));
    }

    @Override
    public Height height() {
        return height;
    }

    @Override
    public Width width() {
        return width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetadataExtractorJpegDimension)) return false;
        MetadataExtractorJpegDimension that = (MetadataExtractorJpegDimension) o;
        return Objects.equals(height, that.height) &&
                Objects.equals(width, that.width);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width);
    }

    @Override
    public String toString() {
        return "MetadataExtractorJpegDimension{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }
}
