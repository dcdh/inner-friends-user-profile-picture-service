package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class HazelcastLockMechanismTest {

    @Inject
    HazelcastLockMechanism hazelcastLockMechanism;

    @Inject
    HazelcastInstance hazelcastInstance;

    @Test
    public void should_lock() throws Exception {
        // Given

        // When
        hazelcastLockMechanism.lock(() -> "pseudoToLock");

        // Then
        assertThat(getLock().isLocked()).isTrue();
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
