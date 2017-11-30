package io.durbs.netstatus.collection.domain.modemstats

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import groovy.transform.Canonical

@Canonical
@DynamoDBTable(tableName='net_stat_tracker_downstream_modem_stats')
class DownstreamChannel extends BaseChannel {

    String signalToNoiseRatio
    String modulation

    Long totalUnerroredCodewords
    Long totalCorrectableCodewords
    Long totalUncorrectableCodewords
}
