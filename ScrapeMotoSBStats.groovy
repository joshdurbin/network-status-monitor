@Grapes([
  @Grab(group='org.jsoup', module='jsoup', version='1.8.1')
])

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import groovy.transform.Canonical
import groovy.transform.ToString
import groovy.json.JsonBuilder

def noBreakSpaceUnicode = '\u00a0'
def noBreakSpaceUnicodeReplacement = ''
def jsoupSelectSkipHeaderQuery = 'td:gt(0)'

def cleanInput = { input ->
  input.replace(noBreakSpaceUnicode, noBreakSpaceUnicodeReplacement).trim()
}

@Canonical
class SignalStatus {

  List<DownstreamChannel> downstreamChannels
  List<UpstreamChannel> upstreamChannels
}

@Canonical
class BaseChannel {

  Integer channelId
  Long frequency
  Integer powerLevel
}

@Canonical
@ToString(includeSuperProperties = true)
class DownstreamChannel extends BaseChannel {

  Integer signalToNoiseRatio
  String modulation
  DownstreamChannelStats stats
}

@Canonical
class DownstreamChannelStats {

  Long totalUnerroredCodewords
  Long totalCorrectableCodewords
  Long totalUncorrectableCodewords
}

@Canonical
@ToString(includeSuperProperties = true)
class UpstreamChannel extends BaseChannel {

  Integer rangingServiceId
  Double symbolRate
  Boolean rangingStatusSuccessful
  String modulation
}

def surfboardCMSignalData = Jsoup.connect('http://192.168.100.1/cmSignalData.htm').get()

// get the three relevant tables
def downstreamTableData = surfboardCMSignalData.select('table:has(th:contains(Downstream))')
def upstreamTableData = surfboardCMSignalData.select('table:has(th:contains(Upstream))')
def downstreamStats = surfboardCMSignalData.select('table:has(th:contains(Signal Stats))')

// get downstream data attributes
def downstreamChannelIds = downstreamTableData.select('tr:has(td:contains(Channel ID))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)
def downstreamFrequencies = downstreamTableData.select('tr:has(td:contains(Frequency))').select(jsoupSelectSkipHeaderQuery).collect { it.text().minus('Hz') }.collect (cleanInput)
def downstreamSignalToNoiseRatios = downstreamTableData.select('tr:has(td:contains(Signal to Noise Ratio))').select(jsoupSelectSkipHeaderQuery).collect { it.text().minus('dB') }.collect (cleanInput)
def downstreamModulation = downstreamTableData.select('tr:has(td:contains(Downstream Modulation))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)
def downstreamPowerLevels = downstreamTableData.select('tr:has(td:contains(Power Level))').select(jsoupSelectSkipHeaderQuery).collect { it.text().minus('dBmV') }.collect (cleanInput)

def downstreamStatsUnerroredCodewords = downstreamStats.select('tr:has(td:contains(Total Unerrored Codewords))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)
def downstreamStatsCorrectableCodewords = downstreamStats.select('tr:has(td:contains(Total Correctable Codewords))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)
def downstreamStatsUncorrectableCodewords = downstreamStats.select('tr:has(td:contains(Total Uncorrectable Codewords))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)

// get upstream data attributes
def upstreamChannelIds = upstreamTableData.select('tr:has(td:contains(Channel ID))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)
def upstreamFrequencies = upstreamTableData.select('tr:has(td:contains(Frequency))').select(jsoupSelectSkipHeaderQuery).collect { it.text().minus('Hz') }.collect (cleanInput)
def upstreamRangingServiceIds = upstreamTableData.select('tr:has(td:contains(Ranging Service ID))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)
def upstreamSymbolRates = upstreamTableData.select('tr:has(td:contains(Symbol Rate))').select(jsoupSelectSkipHeaderQuery).collect { it.text().minus('Msym/sec') }.collect (cleanInput)
def upstreamPowerLevels = upstreamTableData.select('tr:has(td:contains(Power Level))').select(jsoupSelectSkipHeaderQuery).collect { it.text().minus('dBmV') }.collect (cleanInput)
def upstreamModulation = upstreamTableData.select('tr:has(td:contains(Upstream Modulation))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput)
def upstreamRangingStatusSuccessful = upstreamTableData.select('tr:has(td:contains(Ranging Status))').select(jsoupSelectSkipHeaderQuery).collect { it.text() }.collect (cleanInput).collect { it.equals('Success') ? true : false }

def downstreamChannels = []

8.times { counter ->

  downstreamChannels.add(new DownstreamChannel(
    channelId: downstreamChannelIds.get(counter) as Integer,
    frequency: downstreamFrequencies.get(counter) as Long,
    powerLevel: downstreamPowerLevels.get(counter) as Integer,
    signalToNoiseRatio: downstreamSignalToNoiseRatios.get(counter) as Integer,
    modulation: downstreamModulation.get(counter),
    stats: new DownstreamChannelStats(
      totalUnerroredCodewords: downstreamStatsUnerroredCodewords.get(counter) as Long,
      totalCorrectableCodewords: downstreamStatsCorrectableCodewords.get(counter) as Long,
      totalUncorrectableCodewords: downstreamStatsUncorrectableCodewords.get(counter) as Long
    )
  ))
}

def upstreamChannels = []

4.times { counter ->

  upstreamChannels.add(new UpstreamChannel(
    channelId: upstreamChannelIds.get(counter) as Integer,
    frequency: upstreamFrequencies.get(counter) as Long,
    powerLevel: upstreamPowerLevels.get(counter) as Integer,
    rangingServiceId: upstreamRangingServiceIds.get(counter) as Integer,
    symbolRate: upstreamSymbolRates.get(counter) as Double,
    modulation: upstreamModulation.get(counter),
    rangingStatusSuccessful: upstreamRangingStatusSuccessful.get(counter)
  ))
}

def compiledSignalStatus = new SignalStatus(
  downstreamChannels: downstreamChannels,
  upstreamChannels: upstreamChannels)

println new JsonBuilder(compiledSignalStatus).toPrettyString()
