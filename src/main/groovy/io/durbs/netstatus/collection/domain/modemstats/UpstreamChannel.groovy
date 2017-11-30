package io.durbs.netstatus.collection.domain.modemstats

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import groovy.transform.Canonical

@Canonical
@DynamoDBTable(tableName='net_stat_tracker_upstream_modem_stats')
class UpstreamChannel extends BaseChannel {

    Integer rangingServiceId
    String symbolRate
    Boolean rangingStatusSuccessful
    String modulation
}