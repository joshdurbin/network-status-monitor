package io.durbs.netstatus.collection.job

import com.google.common.base.Stopwatch
import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.durbs.netstatus.Configuration
import io.durbs.netstatus.Constants
import io.durbs.netstatus.collection.domain.ModemLogEntry
import io.durbs.netstatus.service.QueuingDynamoDBService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.util.concurrent.TimeUnit

@DisallowConcurrentExecution
@CompileStatic
@Slf4j
class CableModemLogScraper implements Job {

    @Inject
    Configuration config

    @Inject
    QueuingDynamoDBService<ModemLogEntry> modemLogEntriesQueue

    static final Integer EXPECTED_COLUMNS_IN_DATA_ROW = 4

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {

        log.debug('running job...')
        final Stopwatch stopwatch = Stopwatch.createStarted()

        final Document logsData = Jsoup.connect(config.modemLogsLocalEndpoint()).get()

        logsData.select('tr').each { final Element rowElement ->

            Elements columnElements = rowElement.select('td')

            if (columnElements.size() == EXPECTED_COLUMNS_IN_DATA_ROW) {

                modemLogEntriesQueue.offer(new ModemLogEntry(
                        timestamp: Constants.MODEM_EVENT_LOG_DATE_FORMAT.parse(columnElements.get(0).text()),
                        priority: columnElements.get(1).text(),
                        code: columnElements.get(2).text(),
                        message: columnElements.get(3).text()))
            }
        }

        log.info("job finished in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")
    }
}
