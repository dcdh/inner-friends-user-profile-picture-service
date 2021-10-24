package com.innerfriends.userprofilepicture.domain;

public interface Dimension {

    Height height();

    Width width();

    default boolean isValid(final Dimension maxDimension) {
        return height().isLessThanOrEqual(maxDimension.height())
                && width().isLessThanOrEqual(maxDimension.width());
    }

}
