package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.infrastructure.usecase.lock.LockMechanism;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

// locking and release must be called on the same thread !!!
@ApplicationScoped
public class HazelcastLockMechanism implements LockMechanism {

    private static final Logger LOG = Logger.getLogger(HazelcastLockMechanism.class);

    private final HazelcastInstance hazelcastInstance;

    public HazelcastLockMechanism(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    // TODO span interceptor
    @Override
    public void lock(final UserPseudo userPseudo) {
        final FencedLock fencedLock = hazelcastInstance.getCPSubsystem().getLock(userPseudo.pseudo());
        fencedLock.lock();
    }

    // TODO span interceptor
    @Override
    public void release(final UserPseudo userPseudo) {
        final FencedLock fencedLock = hazelcastInstance.getCPSubsystem().getLock(userPseudo.pseudo());
        if (!fencedLock.isLockedByCurrentThread()) {
            throw new IllegalStateException("Must be unlocked by the same thread which has locked it !");
        }
        fencedLock.unlock();
    }

}
