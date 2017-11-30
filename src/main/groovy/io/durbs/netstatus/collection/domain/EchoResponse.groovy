package io.durbs.netstatus.collection.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import groovy.transform.Canonical

import java.time.LocalDateTime

@Canonical
@DynamoDBTable(tableName='net_stat_tracker_echo_responses')
class EchoResponse extends BaseRecord {

    @DynamoDBRangeKey
    String endpoint
    Boolean success
    Long time

    static EchoResponse CREATE_SUCCESSFUL_RESPONSE(String endpoint, Long time) {

        new EchoResponse(timestamp: LocalDateTime.now(), endpoint: endpoint, success: true, time: time)
    }

    static EchoResponse CREATE_UNSUCCESSFUL_RESPONSE(String endpoint) {

        new EchoResponse(timestamp: LocalDateTime.now(), endpoint: endpoint, success: false, time: -1L)
    }

    static EchoResponse CREATE_UNSUCCESSFUL_RESPONSE(String endpoint, Long time) {

        new EchoResponse(timestamp: LocalDateTime.now(), endpoint: endpoint, success: false, time: time)
    }
}
