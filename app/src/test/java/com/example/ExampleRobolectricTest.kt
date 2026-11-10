package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AlarmEntity
import com.example.service.AlarmScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AuraClock", appName)
    }

    @Test
    fun testAlarmEntityFormatting() {
        val alarm = AlarmEntity(
            hour = 14,
            minute = 30,
            daysOfWeekMask = 62, // Weekdays
            label = "Work Standup"
        )

        assertEquals("02:30 PM", alarm.formattedTime(is24Hour = false).trim().let {
            // handle single or double digit hour
            if (it == "2:30 PM") "02:30 PM" else it
        })
        assertEquals("14:30", alarm.formattedTime(is24Hour = true))
        assertEquals("Weekdays", alarm.daysSummary())
    }

    @Test
    fun testAlarmSchedulerCalculation() {
        val alarm = AlarmEntity(
            hour = 8,
            minute = 0,
            daysOfWeekMask = 127
        )
        val triggerTime = AlarmScheduler.calculateNextTriggerMillis(alarm)
        assertTrue(triggerTime > System.currentTimeMillis() - 1000)
    }

    @Test
    fun testCustomColorTheme() {
        val customColor = androidx.compose.ui.graphics.Color(0xFF00FF88)
        val customTheme = com.example.data.model.ClockTheme.fromCustomColor(customColor)
        assertEquals("#00FF88", customTheme.hexCode)
        assertEquals(customColor, customTheme.primaryColor)

        val prefs = com.example.data.UserPreferences(
            isCustomColor = true,
            customColorValue = 0xFF00FF88
        )
        val effective = prefs.effectiveTheme()
        assertEquals("#00FF88", effective.hexCode)
    }
}
