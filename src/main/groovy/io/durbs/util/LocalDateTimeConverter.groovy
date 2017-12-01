package io.durbs.util

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter
import groovy.transform.CompileStatic

import java.time.LocalDateTime

@CompileStatic
class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

    @Override
    String convert(LocalDateTime time) {
        time.toString()
    }

    @Override
    LocalDateTime unconvert(String stringValue) {
        LocalDateTime.parse(stringValue)
    }
}