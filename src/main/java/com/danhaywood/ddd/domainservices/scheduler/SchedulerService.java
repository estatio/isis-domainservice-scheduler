package com.danhaywood.ddd.domainservices.scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.google.common.base.Joiner;

public class SchedulerService {

    static final String USER_KEY = SchedulerService.class.getName() + ".user";
    static final String ROLES_KEY = SchedulerService.class.getName() + ".roles";
    
    private final String user;
    private final String roles;
    
    private Scheduler scheduler;

    ////////////////////////////////////////////////////
    // constructor, init, shutdown
    ////////////////////////////////////////////////////
    
    public SchedulerService(String user, String... roles) {
        this.user = user;
        this.roles = Joiner.on(",").join(roles);
    }

    @PostConstruct
    public void init() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException ex) {
            throw new SchedulerServiceException(ex);
        }
    }

    @PreDestroy
    public void shutdown() {
        shutdownQuartz();
    }
    
    private void shutdownQuartz() {
        if(scheduler == null) {
            return;
        }
        try {
            scheduler.shutdown();
        } catch (SchedulerException se) {
            // ignore
        }
    }

    
    ////////////////////////////////////////////////////
    // API for subclasses 
    ////////////////////////////////////////////////////

    protected void scheduleJob(JobDetail job, Trigger trigger) {
        job.getJobDataMap().put(USER_KEY, user);
        job.getJobDataMap().put(ROLES_KEY, roles);

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            throw new SchedulerServiceException(ex);
        }
    }
}
