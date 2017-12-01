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
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.impl.StdSchedulerFactory

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

        scheduler.start()

        scheduler.scheduleJob(
                newJob(CableModemStatisticsScraper).build(),
                newTrigger().startNow().withSchedule(
                        SimpleScheduleBuilder.repeatHourlyForever(configuration.cableModemStatisticsScraperJobExecutionIntervalInHours())).build())
        scheduler.scheduleJob(
                newJob(CableModemLogScraper).build(),
                newTrigger().startNow().withSchedule(
                        SimpleScheduleBuilder.repeatHourlyForever(configuration.cableModemLogScraperJobExecutionIntervalInHours())).build())
        scheduler.scheduleJob(
                newJob(EchoResponseTracer).build(),
                newTrigger().startNow().withSchedule(
                        SimpleScheduleBuilder.repeatHourlyForever(configuration.echoResponseTracerJobExecutionIntervalInHours())).build())

        scheduler.scheduleJob(
                newJob(DynamoDBQueueFlushJob).build(),
                newTrigger().startNow().withSchedule(
                        SimpleScheduleBuilder.repeatMinutelyForever(configuration.dynamoDBQueueFlushJobExecutionIntervalInMinutes())).build())
    }
}
