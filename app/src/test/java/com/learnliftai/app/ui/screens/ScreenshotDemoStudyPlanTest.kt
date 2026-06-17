package com.learnliftai.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenshotDemoStudyPlanTest {
    @Test
    fun screenshotDemoRequiresDebugBuildAndFlag() {
        assertTrue(ScreenshotDemoStudyPlan.isEnabled(isDebug = true, buildFlagEnabled = true))
        assertFalse(ScreenshotDemoStudyPlan.isEnabled(isDebug = false, buildFlagEnabled = true))
        assertFalse(ScreenshotDemoStudyPlan.isEnabled(isDebug = true, buildFlagEnabled = false))
        assertFalse(ScreenshotDemoStudyPlan.isEnabled(isDebug = false, buildFlagEnabled = false))
    }

    @Test
    fun screenshotDemoHasSevenDaysWithShortTasks() {
        val response = ScreenshotDemoStudyPlan.response("Job Interview Prep")

        assertEquals("Job Interview Prep 7-Day Study Plan", response.title)
        assertEquals(7, response.days.size)
        response.days.forEachIndexed { index, day ->
            assertEquals(index + 1, day.day)
            assertTrue(day.focus.isNotBlank())
            assertTrue(day.tasks.size in 2..3)
        }
    }
}
