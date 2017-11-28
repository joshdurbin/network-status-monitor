package io.durbs.netstatus.service

import com.google.common.collect.ImmutableSet
import groovy.transform.CompileStatic

@CompileStatic
class ImmutableFlushResultBuilder<T> {

    private Set<T> results
    private T failedResult
    private Exception exception

    ImmutableFlushResultBuilder() {
        results = []
    }

    ImmutableFlushResultBuilder<T> addResult(T result) {

        results.add(result)
        this
    }

    ImmutableFlushResultBuilder<T> addFailedResult(T result, Exception resultException) {

        failedResult = result
        exception = resultException

        this
    }

    FlushResult<T> build() {

        new FlushResult<T>(flushedResults: ImmutableSet.copyOf(results), failedResult: failedResult, exception: exception)
    }
}
