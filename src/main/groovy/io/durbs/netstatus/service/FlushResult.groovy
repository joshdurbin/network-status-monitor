package io.durbs.netstatus.service

import groovy.transform.Canonical

@Canonical
class FlushResult<T> {

    Set<T> flushedResults
    T failedResult
    Exception exception

    Boolean success() {

        !failedResult && !exception
    }

    Integer resultCount() {

        flushedResults.size()
    }
}
