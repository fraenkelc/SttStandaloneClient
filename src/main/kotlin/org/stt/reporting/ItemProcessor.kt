package org.stt.reporting

import org.stt.model.TimeTrackingItem

interface ItemProcessor {
    fun process(item: TimeTrackingItem): TimeTrackingItem
}