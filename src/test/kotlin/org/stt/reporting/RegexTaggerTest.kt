package org.stt.reporting

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.stt.model.TimeTrackingItem
import org.stt.reporting.RegexTagger.MatchOrConstant.*
import java.time.LocalDateTime

class RegexTaggerTest {

    @Test
    fun `should assign tags on match`() {
        // GIVEN
        val sut = RegexTagger("matching".toPattern(), Constant("tag"), Constant("value"))
        val item = TimeTrackingItem("this is a matching activity", LocalDateTime.now())
        // WHEN
        val actual = sut.process(item)
        // THEN
        assertThat(actual.tags).containsEntry("tag", "value")
    }

    @Test
    fun `should not assign tags if no match`() {
        // GIVEN
        val sut = RegexTagger("matching".toPattern(), Constant("tag"), Constant("value"))
        val item = TimeTrackingItem("this is an activity", LocalDateTime.now())
        // WHEN
        val actual = sut.process(item)
        // THEN
        assertThat(actual.tags).doesNotContainEntry("tag", "value")
    }

    @Test
    fun `creates tags from numeric group match`() {
        // GIVEN
        val sut = RegexTagger("\\[(\\w+)-(\\d+)]".toPattern(),
            NumericGroupMatch(1), NumericGroupMatch(2))
        val item = TimeTrackingItem("[JIRA-1234] I worked on stuff", LocalDateTime.now())
        // WHEN
        val actual = sut.process(item)
        // THEN
        assertThat(actual.tags).containsEntry("JIRA", "1234")
    }

    @Test
    fun `creates tags from named group match`() {
        // GIVEN
        val sut = RegexTagger("\\[(?<project>\\w+)-(?<number>\\d+)]".toPattern(),
            NamedGroupMatch("project"), NamedGroupMatch("number"))
        val item = TimeTrackingItem("[JIRA-1234] I worked on stuff", LocalDateTime.now())
        // WHEN
        val actual = sut.process(item)
        // THEN
        assertThat(actual.tags).containsEntry("JIRA", "1234")
    }

}