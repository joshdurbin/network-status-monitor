package io.durbs.netstatus.collection.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import groovy.transform.Canonical

@Canonical
@DynamoDBTable(tableName='net_stat_tracker_modem_logs')
class ModemLogEntry extends BaseRecord {

    Date logTimestamp
    String priority
    String code
    String message
}
