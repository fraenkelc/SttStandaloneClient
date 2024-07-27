package org.stt.reporting

import org.stt.model.ReportingItem
import org.stt.model.TimeTrackingItem
import org.stt.text.ItemCategorizer
import org.stt.time.DurationRounder
import java.time.Duration
import java.time.LocalDateTime
import java.util.Comparator.comparing

/**
 * Groups items by the comment of the item: all items with the identical comment get merged into one [ReportingItem].
 * Duration is the sum of all durations of the items.
 *
 * Items without an end date get reported as if the end date was now
 *
 * Items will be returned sorted in ascending order of the comments
 *
 */
class GroupByCommentAggregator(
    private val rounder: DurationRounder, private val categorizer: ItemCategorizer
) : ReportingItemAggregator {
    private val reportItems = HashMap<String, ReportingItem>()

    override fun addItem(item: TimeTrackingItem) {
        val now = LocalDateTime.now()
        val start = item.start
        val end = item.end ?: now

        var duration = Duration.between(start, end)
        if (duration.isNegative) {
            duration = Duration.ZERO
        }
        // assemble
        val comment = item.activity


        val currentItem = reportItems.getOrPut(comment) {
            ReportingItem(
                Duration.ZERO,
                Duration.ZERO,
                comment,
                ItemCategorizer.ItemCategory.BREAK == categorizer.getCategory(comment)
            )
        }
        // overwrite currentItem in the collection and update values
        val newDuration = currentItem.duration.plus(duration)
        reportItems[comment] = currentItem.copy(
            duration = newDuration, roundedDuration = rounder.roundDuration(newDuration)
        )

    }

    override fun getReportItems(): List<ReportingItem> =
        reportItems.values.toList().sortedWith(comparing { it.comment })

}