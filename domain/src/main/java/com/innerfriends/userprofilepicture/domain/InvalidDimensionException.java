package com.innerfriends.userprofilepicture.domain;

import java.util.Objects;

public class InvalidDimensionException extends RuntimeException {

    private final Dimension currentDimension;
    private final Dimension maxDimension;

    public InvalidDimensionException(final Dimension currentDimension, final Dimension maxDimension) {
        this.currentDimension = Objects.requireNonNull(currentDimension);
        this.maxDimension = Objects.requireNonNull(maxDimension);
    }

    public Dimension getCurrentDimension() {
        return currentDimension;
    }

    public Dimension getMaxDimension() {
        return maxDimension;
    }

}
