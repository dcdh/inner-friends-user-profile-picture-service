package com.innerfriends.userprofilepicture.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PositiveIntegerTest {

    @Test
    public void should_be_less() {
        assertThat(new PositiveInteger(10).isLessThanOrEqual(new PositiveInteger(11))).isTrue();
    }

    @Test
    public void should_be_equals() {
        assertThat(new PositiveInteger(10).isLessThanOrEqual(new PositiveInteger(10))).isTrue();
    }

    @Test
    public void should_not_be_less_or_equals() {
        assertThat(new PositiveInteger(10).isLessThanOrEqual(new PositiveInteger(9))).isFalse();
    }

}
