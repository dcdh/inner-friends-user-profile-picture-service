package com.innerfriends.userprofilepicture.infrastructure;

import com.innerfriends.userprofilepicture.domain.UseCaseCommand;
import com.innerfriends.userprofilepicture.domain.UserPseudo;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Objects;

@SingleInstanceUseCaseExecution
@Interceptor
public class SingleInstanceUseCaseExecutionInterceptor {

    private final LockMechanism lockMechanism;

    public SingleInstanceUseCaseExecutionInterceptor(final LockMechanism lockMechanism) {
        this.lockMechanism = Objects.requireNonNull(lockMechanism);
    }

    @AroundInvoke
    public Object execution(final InvocationContext ctx) throws Exception {
        final UserPseudo userPseudo = ((UseCaseCommand) ctx.getParameters()[0]).userPseudo();
        this.lockMechanism.lock(userPseudo);
        final Object ret;
        try {
            ret = ctx.proceed();
        } finally {
            this.lockMechanism.release(userPseudo);
        }
        return ret;
    }

}
