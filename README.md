# isis-domainservice-scheduler #

Domain service providing a background scheduler for [Apache Isis](http://isis.apache.org).

The underlying scheduler implementation is the [Quartz Scheduler](http://quartz-scheduler.org/).

## API ##

The `AbstractSchedulerService` is the base class for an application-specific domain service.  This domain service should:

* provide user credentials through the constructor
* use the inherited `scheduleJob()` to schedule all jobs (using the Quartz API)
* be registered in [the usual way](http://isis.apache.org/applib-guide/domain-services/how-to-09-010-How-to-register-domain-services,-repositories-and-factories.html) in `isis.properties`.

The `AbstractIsisJob` class is the base class for application-specific jobs that need to connect back to a running instance.  It defines a mandatory hook method:

    protected abstract void doExecute(JobExecutionContext context);

Concrete jobs must override this method.  They can optionally use a number of helper methods:

*   protected String getKey(JobExecutionContext context, String key)
  
    To obtain job data from the execution context, using the specified key

*   protected <T> T getService(Class<T> cls)

    Lookup an arbitrary domain service (eg a repository)

## Examples ##

The following example is the demo domain service (as prototyped in [Estatio](https://github.com/estatio/estatio)):

    @Hidden
    public class SchedulerServiceForEstatio extends SchedulerService {

        public SchedulerServiceForEstatio() {
            super("scheduler_user", "scheduler_role", "admin_role");
        }

        public void initializeJobs() {
            JobDetail job = newJob(CountPropertiesJob.class)
                    .withIdentity("job1", "group1")
                    .build();
        
            Trigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(20)
                        .repeatForever())            
                .build();
    
            scheduleJob(job, trigger);
        }
    }

And the following example is the `CountPropertiesJob`, also from Estatio:

    public class CountPropertiesJob extends AbstractIsisJob {

        protected void doExecute(JobExecutionContext context) {
            Properties properties = getService(Properties.class);
            int numProperties = properties.allProperties().size();
        
            System.out.println("number of properties is: " + numProperties);
        }
    }

In the above, `Properties` is a repository for `Property` domain entities.

## Legal Stuff ##
 
### License ###

    Copyright 2013 Dan Haywood

    Licensed under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.


### Dependencies

	<dependencies>
		<dependency>
            <!-- ASL v2.0 -->
			<groupId>org.quartz-scheduler</groupId>
			<artifactId>quartz</artifactId>
			<version>2.1.6</version>
		</dependency>

		<dependency>
            <!-- ASL v2.0 -->
			<groupId>org.apache.isis.core</groupId>
			<artifactId>isis-core-runtime</artifactId>
			<version>${isis.version}</version>
		</dependency>
	</dependencies>

