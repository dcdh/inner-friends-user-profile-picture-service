package com.innerfriends.userprofilepicture.infrastructure.opentelemetry;

import io.opentelemetry.api.trace.Span;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Objects;

@NewSpan
@Interceptor
public class NewSpanExecutionInterceptor {

    private final OpenTelemetryTracingService openTelemetryTracingService;

    public NewSpanExecutionInterceptor(final OpenTelemetryTracingService openTelemetryTracingService) {
        this.openTelemetryTracingService = Objects.requireNonNull(openTelemetryTracingService);
    }

    @AroundInvoke
    public Object execution(final InvocationContext ctx) throws Exception {
        final String spanName = ctx.getTarget().getClass().getSimpleName().replace("_Subclass", "")
                + ":" + ctx.getMethod().getName();
        final Span span = this.openTelemetryTracingService.startANewSpan(spanName);
        try {
            final Object ret = ctx.proceed();
            this.openTelemetryTracingService.endSpan(span);
            return ret;
        } catch (final Exception exception) {
            this.openTelemetryTracingService.markSpanInError(span, exception.getMessage());
            this.openTelemetryTracingService.endSpan(span);
            throw exception;
        }
    }

}
