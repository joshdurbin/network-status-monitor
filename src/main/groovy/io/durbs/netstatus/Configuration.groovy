package io.durbs.netstatus

interface Configuration {

    String awsAccessKey()
    String awsSecretKey()
    String awsRegion()

    String modemStatsLocalEndpoint()
    String modemLogsLocalEndpoint()

    Set<String> echoResponseEndpoints()
    Integer echoRequestsPerEndpoint()
    Integer echoRequestTimeoutInMilliseconds()
}