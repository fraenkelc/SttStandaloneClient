package org.stt.reporting

import org.stt.model.TimeTrackingItem

/**
 * Looks up existing tag values in a map and adds the result as new tags to the [TimeTrackingItem].
 */
class LookupTagger(
    private val dictionary: Map<String, String>,
    private val sourceTag: String,
    private val targetTag: String,
    private val fallbackValue: String? = null
) : ItemProcessor {
    override fun process(item: TimeTrackingItem): TimeTrackingItem {
        val prev = item.tags[sourceTag]
        if (prev != null) {
            val replacement = dictionary[prev]
            (replacement ?: fallbackValue)?.let {
                return item.withMoreTags(mapOf(targetTag to it))
            }
        }
        return item
    }
}