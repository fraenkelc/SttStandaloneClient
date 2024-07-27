package org.stt.reporting

import org.stt.model.ReportingItem
import org.stt.model.TimeTrackingItem

interface ReportingItemAggregator {
    fun addItem(item: TimeTrackingItem): Unit
    fun getReportItems(): List<ReportingItem>
}