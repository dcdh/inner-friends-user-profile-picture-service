package com.innerfriends.userprofilepicture.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DimensionTest {

    public static final class MaxTestDimension implements Dimension {

        @Override
        public Height height() {
            return new Height(768);
        }

        @Override
        public Width width() {
            return new Width(1024);
        }
    }

    public static final class TestDimension implements Dimension {

        private final Height height;
        private final Width width;

        public TestDimension(final Height height, final Width width) {
            this.height = height;
            this.width = width;
        }

        @Override
        public Height height() {
            return height;
        }

        @Override
        public Width width() {
            return width;
        }
    }

    @Test
    public void should_is_valid_when_height_and_width_are_in_max_dimension() {
        assertThat(new TestDimension(new Height(400), new Width(600)).isValid(new MaxTestDimension())).isTrue();
    }

    @Test
    public void should_not_is_valid_when_height_is_over_max_height() {
        assertThat(new TestDimension(new Height(1400), new Width(600)).isValid(new MaxTestDimension())).isFalse();
    }
}
