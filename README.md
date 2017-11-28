# network-status-monitor

## Overview

This is a groovy-based JVM app that aims to observe, track, and transmit network and cable modem statistics. The project leverages quartz to execute jobs that scrape cable modem statistics and logs. In addition, basic network connectivity, functionality is tracked and traced (like "icmp") and pushed to AWS' DynamoDB for long-term storage. The app isn't necessarily mean to be extended or flexible for other users, but it's relatively easy to do so.
 