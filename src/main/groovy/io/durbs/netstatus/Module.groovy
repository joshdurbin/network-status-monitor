package io.durbs.netstatus

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.durbs.netstatus.collection.domain.EchoResponse
import io.durbs.netstatus.collection.domain.ModemLogEntry
import io.durbs.netstatus.collection.domain.modemstats.DownstreamChannel
import io.durbs.netstatus.collection.domain.modemstats.UpstreamChannel
import io.durbs.netstatus.service.QueuingDynamoDBService
import org.cfg4j.provider.ConfigurationProvider
import org.cfg4j.provider.ConfigurationProviderBuilder
import org.cfg4j.source.ConfigurationSource
import org.cfg4j.source.classpath.ClasspathConfigurationSource
import org.cfg4j.source.compose.MergeConfigurationSource
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource

import java.nio.file.Path

@Slf4j
@CompileStatic
class Module extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    Configuration config(ConfigurationProvider configurationProvider) {

        configurationProvider.bind('', Configuration)
    }

    @Provides
    @Singleton
    QueuingDynamoDBService<DownstreamChannel> downstreamChannelQueue(DynamoDBMapper dynamoDBMapper) {
        new QueuingDynamoDBService<DownstreamChannel>(dynamoDBMapper)
    }

    @Provides
    @Singleton
    QueuingDynamoDBService<UpstreamChannel> upstreamChannelQueue(DynamoDBMapper dynamoDBMapper) {
        new QueuingDynamoDBService<UpstreamChannel>(dynamoDBMapper)
    }

    @Provides
    @Singleton
    QueuingDynamoDBService<EchoResponse> echoResponseQueue(DynamoDBMapper dynamoDBMapper) {
        new QueuingDynamoDBService<EchoResponse>(dynamoDBMapper)
    }

    @Provides
    @Singleton
    QueuingDynamoDBService<ModemLogEntry> modemLogEntriesQueue(DynamoDBMapper dynamoDBMapper) {
        new QueuingDynamoDBService<ModemLogEntry>(dynamoDBMapper)
    }

    @Provides
    @Singleton
    ConfigurationProvider configurationProvider() {

        final ConfigFilesProvider classPathVariablesProvider = new ConfigFilesProvider() {

            @Override
            Iterable<Path> getConfigFiles() {
                [new File(Constants.DEFAULT_CLASSPATH_CONFIG_FILE).toPath()]
            }
        }

        final ConfigurationSource configurationSource = new MergeConfigurationSource(
                new ClasspathConfigurationSource(classPathVariablesProvider),
                new EnvironmentVariablesConfigurationSource())

        new ConfigurationProviderBuilder()
                .withConfigurationSource(configurationSource)
                .build()
    }

    @Provides
    @Singleton
    AWSCredentialsProvider awsCreds(Configuration config) {
        new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.awsAccessKey(), config.awsSecretKey()))
    }

    @Provides
    @Singleton
    AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider awsCredentialsProvider, Configuration config) {

        AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(config.awsRegion())
            .withCredentials(awsCredentialsProvider)
            .build()
    }

    @Provides
    @Singleton
    DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {

        new DynamoDBMapper(amazonDynamoDB)
    }
}
