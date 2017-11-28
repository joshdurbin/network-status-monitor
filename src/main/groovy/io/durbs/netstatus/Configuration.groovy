package io.durbs.netstatus

interface Configuration {

    String awsAccessKey()
    String awsSecretKey()
    String awsRegion()

    String cableModemLogScraperJobCronExpression()
    String cableModemStatisticsScraperJobCronExpression()
    String echoResponseTracerJobCronExpression()

    String modemStatsLocalEndpoint()
    String modemLogsLocalEndpoint()

    Set<String> echoResponseEndpoints()
    Integer echoRequestsPerEndpoint()
}