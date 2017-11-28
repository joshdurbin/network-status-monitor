package io.durbs.netstatus.service.job

import com.google.common.base.Stopwatch
import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.durbs.netstatus.collection.domain.EchoResponse
import io.durbs.netstatus.collection.domain.ModemLogEntry
import io.durbs.netstatus.collection.domain.modemstats.DownstreamChannel
import io.durbs.netstatus.collection.domain.modemstats.UpstreamChannel
import io.durbs.netstatus.service.FlushResult
import io.durbs.netstatus.service.QueuingDynamoDBService
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.util.concurrent.TimeUnit

@DisallowConcurrentExecution
@CompileStatic
@Slf4j
class DynamoDBQueueFlushJob implements Job {

    @Inject
    QueuingDynamoDBService<ModemLogEntry> modemLogEntriesService

    @Inject
    QueuingDynamoDBService<DownstreamChannel> downstreamChannelService

    @Inject
    QueuingDynamoDBService<UpstreamChannel> upstreamChannelService

    @Inject
    QueuingDynamoDBService<EchoResponse> echoResponseService

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {

        log.debug('running job...')
        final Stopwatch stopwatch = Stopwatch.createStarted()

        FlushResult<ModemLogEntry> modemLogEntryFlushResult = modemLogEntriesService.flush()
        FlushResult<DownstreamChannel> downstreamChannelFlushResult = downstreamChannelService.flush()
        FlushResult<UpstreamChannel> upstreamChannelFlushResult = upstreamChannelService.flush()
        FlushResult<EchoResponse> echoResponseFlushResult = echoResponseService.flush()

        if (modemLogEntryFlushResult.flushedResults && modemLogEntryFlushResult.success()) {
            log.info("flushed ${modemLogEntryFlushResult.resultCount()} modem log entries")
        }

        if (downstreamChannelFlushResult.flushedResults && downstreamChannelFlushResult.success()) {
            log.info("flushed ${downstreamChannelFlushResult.resultCount()} downstream channel entries")
        }

        if (upstreamChannelFlushResult.flushedResults && upstreamChannelFlushResult.success()) {
            log.info("flushed ${upstreamChannelFlushResult.resultCount()} upstream channel entries")
        }

        if (echoResponseFlushResult.flushedResults && echoResponseFlushResult.success()) {
            log.info("flushed ${echoResponseFlushResult.resultCount()} echo response entries")
        }

        log.info("job finished in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")
    }
}
