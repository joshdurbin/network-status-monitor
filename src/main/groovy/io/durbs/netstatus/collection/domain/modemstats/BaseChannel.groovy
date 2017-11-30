package io.durbs.netstatus.collection.domain.modemstats

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import groovy.transform.Canonical
import io.durbs.netstatus.collection.domain.BaseRecord

@Canonical
@DynamoDBDocument
class BaseChannel extends BaseRecord {

    @DynamoDBRangeKey
    String channel

    String frequency
    String powerLevel
}