package com.innerfriends.userprofilepicture.infrastructure;

import com.innerfriends.userprofilepicture.domain.UserPseudo;

public interface LockMechanism {

    void lock(UserPseudo userPseudo);

    void release(UserPseudo userPseudo);

}
