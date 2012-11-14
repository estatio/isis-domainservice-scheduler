package com.danhaywood.ddd.domainservices.scheduler;

public class SchedulerServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SchedulerServiceException() {
        super();
    }

    public SchedulerServiceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SchedulerServiceException(String arg0) {
        super(arg0);
    }

    public SchedulerServiceException(Throwable arg0) {
        super(arg0);
    }
}