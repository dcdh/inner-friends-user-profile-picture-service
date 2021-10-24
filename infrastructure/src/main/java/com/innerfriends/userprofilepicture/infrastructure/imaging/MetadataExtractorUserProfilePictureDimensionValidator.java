package com.innerfriends.userprofilepicture.infrastructure.imaging;

import com.drew.imaging.ImageProcessingException;
import com.innerfriends.userprofilepicture.domain.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

@ApplicationScoped
public class MetadataExtractorUserProfilePictureDimensionValidator implements UserProfilePictureDimensionValidator {

    private final Logger logger = LoggerFactory.getLogger(MetadataExtractorUserProfilePictureDimensionValidator.class);

    private final Height maxHeight;
    private final Width maxWidth;

    public MetadataExtractorUserProfilePictureDimensionValidator(@ConfigProperty(name = "picture.maxHeight") final Integer maxHeight,
                                                                 @ConfigProperty(name = "picture.maxWidth") final Integer maxWidth) {
        this.maxHeight = new Height(maxHeight);
        this.maxWidth = new Width(maxWidth);
    }

    @Override
    public void validate(byte[] userProfilePicture) throws InvalidDimensionException, UnableToProcessPictureException {
        try {
            final MetadataExtractorJpegDimension metadataExtractorJpegDimension = new MetadataExtractorJpegDimension(userProfilePicture);
            final MaxDimension maxDimension = new MaxDimension(maxHeight, maxWidth);
            if (!metadataExtractorJpegDimension.isValid(maxDimension)) {
                throw new InvalidDimensionException(metadataExtractorJpegDimension, maxDimension);
            }
        } catch (final ImageProcessingException | IOException e) {
            logger.error("Unable to process picture", e);
            throw new UnableToProcessPictureException();
        }
    }

}
