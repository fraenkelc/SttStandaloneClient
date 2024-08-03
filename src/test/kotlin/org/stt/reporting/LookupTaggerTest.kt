package org.stt.reporting

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.stt.model.TimeTrackingItem
import java.time.LocalDateTime

class LookupTaggerTest {

    @Test
    fun `adds new tags for keys in dictionary`() {
        //GIVEN
        val item = TimeTrackingItem("an activity", LocalDateTime.now(), tags = mapOf("Story" to "JIRA-1234"))
        val sut = LookupTagger(mapOf("JIRA-1234" to "EPIC-1"), "Story", "Epic")

        // WHEN
        val actual = sut.process(item)

        // THEN
        assertThat(actual.tags).containsEntry("Epic", "EPIC-1")
    }

    @Test
    fun `continues if source tag does not exist in item`() {
        //GIVEN
        val item = TimeTrackingItem("an activity", LocalDateTime.now(), tags = mapOf("Issue" to "JIRA-1234"))
        val sut = LookupTagger(mapOf("JIRA-1234" to "EPIC-1"), "Story", "Epic")

        // WHEN
        val actual = sut.process(item)

        // THEN
        assertThat(actual).isSameAs(item)
    }

    @Test
    fun `does not add tag if no fallback value is configured`() {
        //GIVEN
        val item = TimeTrackingItem("an activity", LocalDateTime.now(), tags = mapOf("Story" to "JIRA-1235"))
        val sut = LookupTagger(mapOf("JIRA-1234" to "EPIC-1"), "Story", "Epic")

        // WHEN
        val actual = sut.process(item)

        // THEN
        assertThat(actual).isSameAs(item)
    }

    @Test
    fun `uses fallback value if lookup fails`() {
        //GIVEN
        val item = TimeTrackingItem("an activity", LocalDateTime.now(), tags = mapOf("Story" to "JIRA-1235"))
        val sut = LookupTagger(mapOf("JIRA-1234" to "EPIC-1"), "Story", "Epic", fallbackValue = "Other")

        // WHEN
        val actual = sut.process(item)

        // THEN
        assertThat(actual.tags).containsEntry("Epic", "Other")
    }

}