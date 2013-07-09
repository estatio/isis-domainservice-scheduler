/*
 *  Copyright 2013 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package com.danhaywood.isis.domainservice.scheduler;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.google.common.base.Joiner;

public abstract class AbstractSchedulerService {

    static final String USER_KEY = AbstractSchedulerService.class.getName() + ".user";
    static final String ROLES_KEY = AbstractSchedulerService.class.getName() + ".roles";
    
    private final String user;
    private final String roles;
    
    private Scheduler scheduler;

    ////////////////////////////////////////////////////
    // constructor, init, shutdown
    ////////////////////////////////////////////////////
    
    public AbstractSchedulerService(String user, String... roles) {
        this.user = user;
        this.roles = Joiner.on(",").join(roles);
    }

    @PostConstruct
    public void init(Map<String,String> properties) {
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
