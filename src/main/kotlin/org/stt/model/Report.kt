package org.stt.model

import java.time.Duration
import java.time.LocalDateTime

class Report(
    val reportingItems: List<ReportingItem>,
    val start: LocalDateTime?,
    val end: LocalDateTime?,
    val uncoveredDuration: Duration,
    val roundedUncoveredDuration: Duration
)