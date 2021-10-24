package com.innerfriends.userprofilepicture.infrastructure.imaging;

import com.innerfriends.userprofilepicture.domain.Dimension;
import com.innerfriends.userprofilepicture.domain.Height;
import com.innerfriends.userprofilepicture.domain.Width;

import java.util.Objects;

public final class MaxDimension implements Dimension {

    final Height height;
    final Width width;

    public MaxDimension(final Height height, final Width width) {
        this.height = Objects.requireNonNull(height);
        this.width = Objects.requireNonNull(width);
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
        if (!(o instanceof MaxDimension)) return false;
        MaxDimension that = (MaxDimension) o;
        return Objects.equals(height, that.height) &&
                Objects.equals(width, that.width);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width);
    }
}
