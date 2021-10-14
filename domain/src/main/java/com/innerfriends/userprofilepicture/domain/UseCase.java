package com.innerfriends.userprofilepicture.domain;

public interface UseCase<R, C extends UseCaseCommand> {

    R execute(C command);

}
