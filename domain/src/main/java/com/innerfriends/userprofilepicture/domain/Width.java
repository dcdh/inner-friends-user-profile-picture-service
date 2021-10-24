package com.innerfriends.userprofilepicture.domain;

public final class Width extends PositiveInteger {

    public Width(final Integer value) {
        super(value);
    }

    public boolean isLessThanOrEqual(final Width width) {
        return super.isLessThanOrEqual(width);
    }

}
