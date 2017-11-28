package io.durbs.netstatus

interface Configuration {

    String awsAccessKey()
    String awsSecretKey()
    String awsRegion()

    Integer cableModemLogScraperJobExecutionIntervalInHours()
    Integer cableModemStatisticsScraperJobExecutionIntervalInHours()
    Integer echoResponseTracerJobExecutionIntervalInHours()
    Integer dynamoDBQueueFlushJobExecutionIntervalInMinutes()

    String modemStatsLocalEndpoint()
    String modemLogsLocalEndpoint()

    Set<String> echoResponseEndpoints()
    Integer echoRequestsPerEndpoint()
}