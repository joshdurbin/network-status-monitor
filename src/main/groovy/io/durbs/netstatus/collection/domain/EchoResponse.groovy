package io.durbs.netstatus.collection.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import groovy.transform.Canonical

@Canonical
@DynamoDBTable(tableName='echo_responses')
class EchoResponse {

    @DynamoDBHashKey
    Date timestamp

    @DynamoDBRangeKey
    String endpoint
    Boolean success
    Long time

    static EchoResponse CREATE_SUCCESSFUL_RESPONSE(String endpoint, Long time) {

        new EchoResponse(timestamp: new Date(), endpoint: endpoint, success: true, time: time)
    }

    static EchoResponse CREATE_UNSUCCESSFUL_RESPONSE(String endpoint) {

        new EchoResponse(timestamp: new Date(), endpoint: endpoint, success: false, time: -1L)
    }

    static EchoResponse CREATE_UNSUCCESSFUL_RESPONSE(String endpoint, Long time) {

        new EchoResponse(timestamp: new Date(), endpoint: endpoint, success: false, time: time)
    }
}
