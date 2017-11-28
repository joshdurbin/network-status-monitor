package io.durbs.netstatus

import com.google.inject.Guice
import com.google.inject.Injector
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.durbs.netstatus.collection.job.CableModemLogScraper
import io.durbs.netstatus.collection.job.CableModemStatisticsScraper
import io.durbs.netstatus.service.job.DynamoDBQueueFlushJob
import io.durbs.netstatus.collection.job.EchoResponseTracer
import io.durbs.netstatus.factory.GuiceJobFactory
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.impl.StdSchedulerFactory

import static org.quartz.CronScheduleBuilder.cronSchedule
import static org.quartz.JobBuilder.newJob
import static org.quartz.TriggerBuilder.newTrigger

@Slf4j
@CompileStatic
class Main {

    static void main(String[] args) {

        final Injector injector = Guice.createInjector(new Module())

        final Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler()
        final Configuration configuration = injector.getInstance(Configuration)

        scheduler.setJobFactory(injector.getInstance(GuiceJobFactory))

        final JobDetail cableModemStatisticsScraperJob = newJob (CableModemStatisticsScraper).build()
        final JobDetail cableModemLogScraperJob = newJob (CableModemLogScraper).build()
        final JobDetail echoResponseTracer = newJob (EchoResponseTracer).build()
        final JobDetail persistenceJob = newJob(DynamoDBQueueFlushJob).build()

        scheduler.start()

        scheduler.scheduleJob(cableModemStatisticsScraperJob, newTrigger().startNow().withSchedule(cronSchedule(configuration.cableModemStatisticsScraperJobCronExpression())).build())
        scheduler.scheduleJob(cableModemLogScraperJob, newTrigger().startNow().withSchedule(cronSchedule(configuration.cableModemLogScraperJobCronExpression())).build())
        scheduler.scheduleJob(echoResponseTracer, newTrigger().startNow().withSchedule(cronSchedule(configuration.echoResponseTracerJobCronExpression())).build())

        scheduler.scheduleJob(persistenceJob, newTrigger().startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(Constants.FLUSH_JOB_INTERVAL_IN_SECONDS)).build())
    }
}
