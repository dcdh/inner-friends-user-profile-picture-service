package com.innerfriends.userprofilepicture.infrastructure.opentelemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class OpenTelemetryTracingService {

    private final Tracer tracer;

    public OpenTelemetryTracingService(final Tracer tracer) {
        this.tracer = Objects.requireNonNull(tracer);
    }

    public Span startANewSpan(final String spanName) {
        final Span parentSpan = Span.current();
        return tracer.spanBuilder(spanName)
                .setParent(Context.current().with(parentSpan))
                .startSpan();
    }

    public void endSpan(final Span span) {
        span.end();
    }

    public void markSpanInError(final Span span, final String errorMessage) {
        span.setStatus(StatusCode.ERROR);
        span.setAttribute("errorMessage", errorMessage);
    }

}
