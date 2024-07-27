package org.stt.reporting

import org.stt.model.Report
import org.stt.model.TimeTrackingItem
import org.stt.time.DurationRounder
import java.time.Duration
import java.time.LocalDateTime
import java.util.stream.Stream

/**
 * Reads all elements from the given reader and produces a [Report].
 *
 * Items are processed with the itemProcessors first and then aggregated and grouped by the itemAggregator.
 */
class SummingReportGenerator(
    private val itemsToRead: Stream<TimeTrackingItem>,
    private val rounder: DurationRounder,
    private val itemProcessors : List<(TimeTrackingItem) -> TimeTrackingItem> = emptyList(),
    private val itemAggregator: ReportingItemAggregator
) : ReportGenerator {

    override fun createReport(): Report {
        var startOfReport: LocalDateTime? = null
        var endOfReport: LocalDateTime? = null

        var uncoveredDuration = Duration.ZERO
        var lastItem: TimeTrackingItem? = null

        itemsToRead.map { itemProcessors.fold(it) { acc, processor -> processor(acc) } }.use { items ->
            val it = items.iterator()
            while (it.hasNext()) {
                val item = it.next()
                val now = LocalDateTime.now()
                val start = item.start
                val end = item.end ?: now

                if (lastItem != null) {
                    val endOfLastItem = lastItem!!.end ?: now
                    if (endOfLastItem.isBefore(start)) {
                        val additionalUncoveredTime = Duration.between(
                            endOfLastItem, start
                        )
                        uncoveredDuration = uncoveredDuration
                            .plus(additionalUncoveredTime)
                    }
                }

                lastItem = item

                if (startOfReport == null) {
                    startOfReport = start
                }
                endOfReport = end

                itemAggregator.addItem(item)
            }

        }

        return Report(
            itemAggregator.getReportItems(), startOfReport, endOfReport,
            uncoveredDuration, rounder.roundDuration(uncoveredDuration)
        )
    }


}
