package io.durbs.netstatus

import com.google.inject.Guice
import com.google.inject.Injector
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.durbs.netstatus.job.CableModemLogScraper
import io.durbs.netstatus.job.CableModemStatisticsScraper
import io.durbs.netstatus.job.EchoResponseTracer
import io.durbs.netstatus.job.factory.GuiceJobFactory
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory

import static org.quartz.JobBuilder.newJob

@Slf4j
@CompileStatic
class Main {

    static void main(String[] args) {

        final Injector injector = Guice.createInjector(new Module())

        final Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler()

        scheduler.setJobFactory(injector.getInstance(GuiceJobFactory))

        final JobDetail cableModemStatisticsScraperJob = newJob (CableModemStatisticsScraper).build()
        final JobDetail cableModemLogScraperJob = newJob (CableModemLogScraper).build()
        final JobDetail echoResponseTracer = newJob (EchoResponseTracer).build()

        scheduler.scheduleJob(cableModemStatisticsScraperJob, Util.generateQuartzTrigger())
        scheduler.scheduleJob(cableModemLogScraperJob, Util.generateQuartzTrigger())
        scheduler.scheduleJob(echoResponseTracer, Util.generateQuartzTrigger())

        scheduler.start()
    }
}
