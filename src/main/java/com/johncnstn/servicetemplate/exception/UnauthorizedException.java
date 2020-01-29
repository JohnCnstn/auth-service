package com.johncnstn.servicetemplate.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UnauthorizedException extends AbstractThrowableProblem {

    public UnauthorizedException(final String message) {
        super(ErrorConstants.UNAUTHORIZED, "Unauthorized", Status.UNAUTHORIZED, message);
    }

}
