package com.danhaywood.ddd.domainservices.scheduler;

import java.util.List;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.runtime.authentication.standard.SimpleSession;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.session.IsisSession;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public abstract class AbstractIsisJob implements Job {

    /**
     * Sets up an {@link IsisSession} then delegates to the {@link #doExecute(JobExecutionContext) hook}. 
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final AuthenticationSession authSession = newAuthSession(context);
        try {
            IsisContext.openSession(authSession);
            doExecute(context);
        } finally {
            IsisContext.closeSession();
        }
    }

    AuthenticationSession newAuthSession(JobExecutionContext context) {
        String user = getKey(context, SchedulerService.USER_KEY);
        String rolesStr = getKey(context, SchedulerService.ROLES_KEY);
        String[] roles = Iterables.toArray(
                Splitter.on(",").split(rolesStr), String.class);
        return new SimpleSession(user, roles);
    }

    
    /**
     * Mandatory hook.
     */
    protected abstract void doExecute(JobExecutionContext context);

    /**
     * Helper method for benefit of subclasses
     */
    protected String getKey(JobExecutionContext context, String key) {
        return context.getMergedJobDataMap().getString(key);
    }

    /**
     * Helper method for benefit of subclasses
     */
    protected <T> T getService(Class<T> cls) {
        List<Object> services = IsisContext.getServices();
        for (Object service : services) {
            if(cls.isAssignableFrom(service.getClass())) {
                return asT(service);
            }
        }
        throw new IllegalArgumentException("No service of type '" + cls.getName() + "' was found");
    }

    @SuppressWarnings("unchecked")
    private static <T> T asT(Object service) {
        return (T) service;
    }

}
