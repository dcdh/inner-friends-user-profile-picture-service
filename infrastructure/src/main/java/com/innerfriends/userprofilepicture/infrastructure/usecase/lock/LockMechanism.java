package com.innerfriends.userprofilepicture.infrastructure.usecase.lock;

import com.innerfriends.userprofilepicture.domain.UserPseudo;

public interface LockMechanism {

    void lock(UserPseudo userPseudo);

    void release(UserPseudo userPseudo);

}
