package io.durbs.netstatus.collection.domain.modemstats

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import groovy.transform.Canonical

@Canonical
@DynamoDBDocument
class BaseChannel {

    @DynamoDBHashKey
    Date timestamp

    @DynamoDBRangeKey
    String channel

    String frequency
    String powerLevel
}