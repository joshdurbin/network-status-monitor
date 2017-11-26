# network-status-monitor

## Overview

This project builds a java background app, that runs a series of quartz jobs on a specified interval. The jobs aim to do a few things:

1. Scrape up and downstream channel statistics from a local Motorola Surfboard cable modem
2. Scrape logs from a local Motorola cable modem
3. Issue layer 3 ICMP or layer 4 socket connections to DNS servers for "ICMP" echo performance tracking

The data is transmitted to DynamoDB for storage.

## Configuration (required)

The app requires the following parameters are set within the environment:

1. awsAccessKey
2. awsSecretKey
3. awsRegion

## Configuration (optional)

The following parameters can optionally be defined within the environment:

1. modemStatsLocalEndpoint
2. modemLogsLocalEndpoint
3. echoResponseEndpoints (set of strings)
4. echoRequestsPerEndpoint
5. echoRequestTimeoutInMilliseconds
