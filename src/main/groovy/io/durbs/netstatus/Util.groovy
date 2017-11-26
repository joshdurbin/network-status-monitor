package io.durbs.netstatus

import groovy.transform.CompileStatic
import org.quartz.Trigger

import static org.quartz.SimpleScheduleBuilder.simpleSchedule
import static org.quartz.TriggerBuilder.newTrigger

@Singleton
@CompileStatic
class Util {

    static Trigger generateQuartzTrigger() {

        newTrigger()
                .startNow()
                .withSchedule(simpleSchedule()
                    .withIntervalInMinutes(Constants.POLL_INTERVAL_IN_MINUTES)
                    .repeatForever())
                .build()
    }
}
