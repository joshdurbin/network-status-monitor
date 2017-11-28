package io.durbs.netstatus

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

@Singleton
@CompileStatic
class Constants {

    static final String DEFAULT_CLASSPATH_CONFIG_FILE = 'defaultConfig.yaml'

    static final String MODEM_EVENT_LOG_DATE_FORMAT_EXPRESSION = 'MMM dd yyyy HH:mm:ss'
    static final SimpleDateFormat MODEM_EVENT_LOG_DATE_FORMAT = new SimpleDateFormat(MODEM_EVENT_LOG_DATE_FORMAT_EXPRESSION)
}
