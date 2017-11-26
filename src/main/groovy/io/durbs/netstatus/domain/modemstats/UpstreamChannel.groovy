package io.durbs.netstatus.domain.modemstats

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import groovy.transform.Canonical

@Canonical
@DynamoDBTable(tableName='upstream_cable_modem_statistics')
class UpstreamChannel extends BaseChannel {

    Integer rangingServiceId
    String symbolRate
    Boolean rangingStatusSuccessful
    String modulation
}