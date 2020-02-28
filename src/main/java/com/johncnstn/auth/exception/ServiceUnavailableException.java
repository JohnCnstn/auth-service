package com.johncnstn.auth.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ServiceUnavailableException extends AbstractThrowableProblem {

    public ServiceUnavailableException(final String message) {
        super(
                ErrorConstants.SERVICE_UNAVAILABLE,
                "Service Unavailable",
                Status.SERVICE_UNAVAILABLE,
                message);
    }
}
