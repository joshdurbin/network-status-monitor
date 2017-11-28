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

    Integer flush() {

        Integer flushCount = 0

        T objectToSave

        try {

            while (queue.peek()) {

                objectToSave = queue.poll()
                dynamoDBMapper.save(objectToSave)
                ++flushCount
            }

        } catch (Exception exception) {

            log.error("An error occurred trying to save ${objectToSave}, offering object back to queue", exception)
            flushCount = -1
        }

        flushCount
    }
}
