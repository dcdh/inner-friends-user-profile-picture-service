package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import com.innerfriends.userprofilepicture.infrastructure.opentelemetry.OpenTelemetryTracingService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
public class HazelcastLockMechanismTest {

    @Inject
    HazelcastLockMechanism hazelcastLockMechanism;

    @Inject
    HazelcastInstance hazelcastInstance;

    @InjectMock
    OpenTelemetryTracingService openTelemetryTracingService;

    @Test
    public void should_lock() throws Exception {
        // Given

        // When
        hazelcastLockMechanism.lock(() -> "pseudoToLock");

        // Then
        assertThat(getLock().isLocked()).isTrue();
        verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        verify(openTelemetryTracingService, times(1)).endSpan(any());
        unlock();
    }

    @Test
    public void should_release() throws Exception {
        // Given
        lock();

        // When
        hazelcastLockMechanism.release(() -> "pseudoToLock");

        // Then
        assertThat(getLock().isLocked()).isFalse();
        verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    private FencedLock getLock() {
        return hazelcastInstance.getCPSubsystem().getLock("pseudoToLock");
    }

    private void lock() throws Exception {
        hazelcastInstance.getCPSubsystem().getLock("pseudoToLock").lock();
    }

    private void unlock() throws Exception {
        hazelcastInstance.getCPSubsystem().getLock("pseudoToLock").unlock();
    }

}
