package io.durbs.netstatus.service

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class QueuingDynamoDBService<T> {

    final DynamoDBMapper dynamoDBMapper

    @Delegate
    final Queue<T> queue = new LinkedList<T>()

    QueuingDynamoDBService(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper
    }

    FlushResult<T> flush() {

        ImmutableFlushResultBuilder<T> resultBuilder = new ImmutableFlushResultBuilder<>()

        T objectToSave

        try {

            while (queue.peek()) {

                objectToSave = queue.poll()

                dynamoDBMapper.save(objectToSave)
                resultBuilder.addResult(objectToSave)
            }

        } catch (Exception exception) {

            log.error("An error occurred trying to save ${objectToSave}, offering object back to queue", exception)
            queue.offer(objectToSave)
            resultBuilder.addFailedResult(objectToSave, exception)
        }

        resultBuilder.build()
    }
}
