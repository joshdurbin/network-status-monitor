package io.durbs.netstatus.job

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.google.common.base.Stopwatch
import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.durbs.netstatus.Configuration
import io.durbs.netstatus.domain.EchoResponse
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import java.nio.channels.SocketChannel
import java.util.concurrent.TimeUnit

@DisallowConcurrentExecution
@CompileStatic
@Slf4j
class EchoResponseTracer implements Job {

    @Inject
    Configuration config

    @Inject
    DynamoDBMapper dynamoDBMapper

    private static final Integer DNS_PORT = 53

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {

        log.debug('running job...')
        final Stopwatch stopwatch = Stopwatch.createStarted()

        config.echoResponseEndpoints().each { String endpoint ->

            config.echoRequestsPerEndpoint().times {

                final EchoResponse echoResponse = socketLayerFourTest(endpoint, DNS_PORT)
                log.info("echo to ${echoResponse.endpoint} was ${echoResponse ? 'successful' : 'unsuccessful'} in ${echoResponse.time} ms")

                dynamoDBMapper.save(echoResponse)
            }
        }

        log.info("job finished in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")
    }

    EchoResponse icmpLayerThreeTest(String hostAddress) {

        try {

            InetAddress inetAddress = InetAddress.getByName(hostAddress)
            Stopwatch stopwatch = Stopwatch.createStarted()

            if (inetAddress.isReachable(config.echoRequestTimeoutInMilliseconds())) {
                EchoResponse.CREATE_SUCCESSFUL_RESPONSE(hostAddress, stopwatch.elapsed(TimeUnit.MILLISECONDS))
            } else {
                EchoResponse.CREATE_UNSUCCESSFUL_RESPONSE(hostAddress, stopwatch.elapsed(TimeUnit.MILLISECONDS))
            }

        } catch (Exception exception) {

            log.error("An exception occurred for endpoint '${hostAddress}'", exception)
            EchoResponse.CREATE_UNSUCCESSFUL_RESPONSE(hostAddress)
        }
    }

    EchoResponse socketLayerFourTest(String hostAddress, Integer port) {

        SocketChannel socketChannel = SocketChannel.open()
        socketChannel.configureBlocking(true)

        try {

            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(hostAddress), port)
            Stopwatch stopwatch = Stopwatch.createStarted()

            if (socketChannel.connect(socketAddress)) {
                EchoResponse.CREATE_SUCCESSFUL_RESPONSE(hostAddress, stopwatch.elapsed(TimeUnit.MILLISECONDS))
            } else {
                EchoResponse.CREATE_UNSUCCESSFUL_RESPONSE(hostAddress, stopwatch.elapsed(TimeUnit.MILLISECONDS))
            }

        } catch (Exception exception) {

            log.error("An exception occurred for endpoint '${hostAddress}'", exception)
            EchoResponse.CREATE_UNSUCCESSFUL_RESPONSE(hostAddress)

        } finally {

            socketChannel?.close()
        }
    }
}
