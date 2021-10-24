package com.innerfriends.userprofilepicture.domain;

import java.util.Objects;

public class PositiveInteger {

    private final Integer value;

    public PositiveInteger(final Integer value) {
        this.value = Objects.requireNonNull(value);
        if (value < 0) {
            throw new IllegalStateException();
        }
    }

    public Integer getValue() {
        return value;
    }

    protected boolean isLessThanOrEqual(final PositiveInteger positiveInteger) {
        return this.value.compareTo(positiveInteger.value) <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositiveInteger)) return false;
        PositiveInteger that = (PositiveInteger) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PositiveInteger{" +
                "value=" + value +
                '}';
    }
}
