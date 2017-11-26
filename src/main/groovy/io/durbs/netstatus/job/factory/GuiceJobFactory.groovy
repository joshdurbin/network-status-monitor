package io.durbs.netstatus.job.factory

import com.google.inject.Inject
import com.google.inject.Injector
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.quartz.Job
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle

@CompileStatic
@Slf4j
class GuiceJobFactory implements JobFactory {

    @Inject
    private Injector guice

    GuiceJobFactory() {

    }

    @Override
    Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {

        final JobDetail jobDetail = triggerFiredBundle.getJobDetail()
        final Class jobClass = jobDetail.getJobClass()

        try {

            (Job) guice.getInstance(jobClass)

        } catch (Exception exception) {

            log.error("job factory failed to gain a handle on an instance of class ${jobClass.name}", exception)

            throw new UnsupportedOperationException(exception)
        }
    }
}