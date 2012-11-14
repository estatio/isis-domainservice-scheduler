package com.danhaywood.ddd.domainservices.scheduler;

import org.quartz.JobExecutionContext;

public class HelloJob extends AbstractIsisJob {

    protected void doExecute(JobExecutionContext context) {
        System.out.println("hello world");
    }

}
