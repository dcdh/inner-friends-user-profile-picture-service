package com.innerfriends.userprofilepicture.domain;

public final class Height extends PositiveInteger {

    public Height(Integer value) {
        super(value);
    }

    public boolean isLessThanOrEqual(final Height height) {
        return super.isLessThanOrEqual(height);
    }

}
