package io.durbs.netstatus.collection.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import groovy.transform.Canonical

@Canonical
@DynamoDBTable(tableName='modem_log_entries')
class ModemLogEntry {

    @DynamoDBHashKey
    Date timestamp

    String priority
    String code
    String message
}
