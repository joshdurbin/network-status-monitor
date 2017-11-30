package io.durbs.netstatus.collection.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted
import groovy.transform.Canonical
import io.durbs.netstatus.Constants
import io.durbs.util.LocalDateTimeConverter

import java.time.LocalDateTime
import java.time.ZoneOffset

@Canonical
@DynamoDBDocument
class BaseRecord {

    @DynamoDBHashKey
    @DynamoDBTypeConverted(converter=LocalDateTimeConverter)
    LocalDateTime timestamp

    @DynamoDBAttribute
    Integer getExpirationTTL() {

        timestamp.plusDays(Constants.DYNAMODB_RECORD_TTL_EXPIRATION_IN_DAYS).toInstant(ZoneOffset.UTC).getEpochSecond() as Integer
    }
}
