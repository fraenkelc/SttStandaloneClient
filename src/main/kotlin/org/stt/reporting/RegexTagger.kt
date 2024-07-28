package org.stt.reporting

import org.stt.model.TimeTrackingItem
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Tags [TimeTrackingItem] with tags and values by running the specified regex on each activity.
 * Supports both extracted and constant values for tag and values.
 * Even if both tag and value are constant the tag is only applied if the pattern is found in activity.
 */
class RegexTagger(
    private val pattern: Pattern,
    private val tagName: MatchOrConstant,
    private val tagValue: MatchOrConstant
) {
    fun process(item: TimeTrackingItem): TimeTrackingItem {
        val matcher = pattern.matcher(item.activity)
        if (matcher.find()) {
            val name = tagName.evaluate(matcher)
            val value = tagValue.evaluate(matcher)
            return item.withMoreTags(mapOf(name to value))
        }
        return item
    }

    sealed class MatchOrConstant {
        data class NamedGroupMatch(private val group: String) : MatchOrConstant() {
            fun extract(matcher: Matcher): String = matcher.group(group)
        }
        data class NumericGroupMatch(private val group: Int) : MatchOrConstant() {
            fun extract(matcher: Matcher): String = matcher.group(group)
        }

        data class Constant(val value: String) : MatchOrConstant()

        fun evaluate(matcher: Matcher) = when (this) {
            is Constant -> this.value
            is NamedGroupMatch -> this.extract(matcher)
            is NumericGroupMatch -> this.extract(matcher)
        }
    }
}