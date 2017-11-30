package io.durbs.netstatus.collection.job

import com.google.common.base.Stopwatch
import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.durbs.netstatus.Configuration
import io.durbs.netstatus.collection.domain.modemstats.DownstreamChannel

import io.durbs.netstatus.collection.domain.modemstats.UpstreamChannel
import io.durbs.netstatus.service.QueuingDynamoDBService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Slf4j
@CompileStatic
@DisallowConcurrentExecution
class CableModemStatisticsScraper implements Job {

    static final String UNICODE_NO_BREAK_SPACE = '\u00a0'
    static final String EMPTY_STRING = ''
    static final String JSOUP_SELECT_SKIP_HEADER_QUERY = 'td:gt(0)'

    @Inject
    Configuration config

    @Inject
    QueuingDynamoDBService<DownstreamChannel> downstreamChannelQueue

    @Inject
    QueuingDynamoDBService<UpstreamChannel> upstreamChannelQueue

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {

        log.debug('running job...')
        final Stopwatch stopwatch = Stopwatch.createStarted()

        final Document surfboardCMSignalData = Jsoup.connect(config.modemStatsLocalEndpoint()).get()

        final LocalDateTime executionTime = LocalDateTime.now()

        scrapeDownstreamChannels(surfboardCMSignalData, executionTime).each { DownstreamChannel downstreamChannel ->
            downstreamChannelQueue.offer(downstreamChannel)
        }

        scrapeUpstreamChannels(surfboardCMSignalData, executionTime).each { UpstreamChannel upstreamChannel ->
            upstreamChannelQueue.offer(upstreamChannel)
        }

        log.info("job finished in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")
    }

    static List<DownstreamChannel> scrapeDownstreamChannels(final Document document, final LocalDateTime executionTime) {

        final Elements downstreamTableData = document.select('table:has(th:contains(Downstream))')
        final Elements downstreamStats = document.select('table:has(th:contains(Signal Stats))')

        final List<String> downstreamChannelIds = cleanElementInput(downstreamTableData.select('tr:has(td:contains(Channel ID))'))
        final List<String> downstreamFrequencies = cleanElementInput(downstreamTableData.select('tr:has(td:contains(Frequency))'))
        final List<String> downstreamSignalToNoiseRatios = cleanElementInput(downstreamTableData.select('tr:has(td:contains(Signal to Noise Ratio))'))
        final List<String> downstreamModulation = cleanElementInput(downstreamTableData.select('tr:has(td:contains(Downstream Modulation))'))
        final List<String> downstreamPowerLevels = cleanElementInput(downstreamTableData.select('tr:has(td:contains(Power Level))'))

        final List<String> downstreamStatsUnerroredCodewords = cleanElementInput(downstreamStats.select('tr:has(td:contains(Total Unerrored Codewords))'))
        final List<String> downstreamStatsCorrectableCodewords = cleanElementInput(downstreamStats.select('tr:has(td:contains(Total Correctable Codewords))'))
        final List<String> downstreamStatsUncorrectableCodewords = cleanElementInput(downstreamStats.select('tr:has(td:contains(Total Uncorrectable Codewords))'))

        final List<DownstreamChannel> channels = []

        downstreamChannelIds.size().times { Integer counter ->

            channels.add(new DownstreamChannel(

                    timestamp: executionTime,
                    channel: downstreamChannelIds.get(counter),
                    frequency: downstreamFrequencies.get(counter),
                    powerLevel: downstreamPowerLevels.get(counter),
                    signalToNoiseRatio: downstreamSignalToNoiseRatios.get(counter),
                    modulation: downstreamModulation.get(counter),
                    totalUnerroredCodewords: downstreamStatsUnerroredCodewords.get(counter) as Long,
                    totalCorrectableCodewords: downstreamStatsCorrectableCodewords.get(counter) as Long,
                    totalUncorrectableCodewords: downstreamStatsUncorrectableCodewords.get(counter) as Long
            ))
        }

        channels
    }

    static List<UpstreamChannel> scrapeUpstreamChannels(final Document document, final LocalDateTime executionTime) {

        final Elements upstreamTableData = document.select('table:has(th:contains(Upstream))')

        final List<String> upstreamChannelIds = cleanElementInput(upstreamTableData.select('tr:has(td:contains(Channel ID))'))
        final List<String> upstreamFrequencies = cleanElementInput(upstreamTableData.select('tr:has(td:contains(Frequency))'))
        final List<String> upstreamRangingServiceIds = cleanElementInput(upstreamTableData.select('tr:has(td:contains(Ranging Service ID))'))
        final List<String> upstreamSymbolRates = cleanElementInput(upstreamTableData.select('tr:has(td:contains(Symbol Rate))'))
        final List<String> upstreamPowerLevels = cleanElementInput(upstreamTableData.select('tr:has(td:contains(Power Level))'))
        final List<String> upstreamModulation = cleanElementInput(upstreamTableData.select('tr:has(td:contains(Upstream Modulation))'))
        final List<Boolean> upstreamRangingStatusSuccessful = cleanElementInput(upstreamTableData.select('tr:has(td:contains(Ranging Status))')).collect { it.equals('Success') }

        final List<UpstreamChannel> channels = []

        upstreamChannelIds.size().times { Integer counter ->

            channels.add(new UpstreamChannel(

                    timestamp: executionTime,
                    channel: upstreamChannelIds.get(counter),
                    frequency: upstreamFrequencies.get(counter),
                    powerLevel: upstreamPowerLevels.get(counter),
                    rangingServiceId: upstreamRangingServiceIds.get(counter) as Integer,
                    symbolRate: upstreamSymbolRates.get(counter),
                    modulation: upstreamModulation.get(counter),
                    rangingStatusSuccessful: upstreamRangingStatusSuccessful.get(counter) as Boolean
            ))
        }

        channels
    }

    static List<String> cleanElementInput(Elements elements) {

        elements.select(JSOUP_SELECT_SKIP_HEADER_QUERY).collect { Element element ->
            element.text().replace(UNICODE_NO_BREAK_SPACE, EMPTY_STRING).trim()
        }
    }
}
