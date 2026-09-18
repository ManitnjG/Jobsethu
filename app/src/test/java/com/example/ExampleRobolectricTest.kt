package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Job
import com.example.data.model.SourceClassification
import com.example.data.provider.DuplicateJobDetector
import com.example.data.provider.ScamProtectionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("JobSetu AI", appName)
    }

    @Test
    fun `test duplicate job detector groups cross-posted jobs`() {
        val job1 = Job(
            id = "j1",
            title = "Tour Operations Executive",
            company = "MakeMyTrip India Pvt Ltd",
            locationCity = "Chennai",
            locationState = "Tamil Nadu",
            description = "Coordinates travel",
            sourceName = "Naukri",
            sourceUrl = "https://naukri.com/mmt",
            sourceClassification = SourceClassification.EXTERNAL_APPLICATION,
            isOfficialEmployerPage = false
        )

        val job2 = Job(
            id = "j2",
            title = "Tour Operations Executive",
            company = "MakeMyTrip India",
            locationCity = "Chennai",
            locationState = "Tamil Nadu",
            description = "Coordinates travel and hotels",
            sourceName = "MakeMyTrip Careers",
            sourceUrl = "https://makemytrip.com/careers",
            sourceClassification = SourceClassification.EMPLOYER_CAREER_INTEGRATION,
            isOfficialEmployerPage = true
        )

        val consolidated = DuplicateJobDetector.consolidateDuplicates(listOf(job1, job2))
        assertEquals(1, consolidated.size)
        val primary = consolidated.first()
        assertTrue(primary.isOfficialEmployerPage)
        assertEquals("MakeMyTrip Careers", primary.sourceName)
        assertEquals(1, primary.duplicateSources.size)
    }

    @Test
    fun `test scam protection flags security deposits and whatsapp only webmail`() {
        val scamCheck = ScamProtectionEngine.evaluateJob(
            title = "Data Entry Operator",
            company = "Quick Solutions",
            description = "Earn ₹50,000 weekly from home. Registration fee ₹1000 refundable deposit required. Contact on WhatsApp only: 9876543210 (recruiter@gmail.com).",
            salaryLpa = 12.0,
            minExpYears = 0,
            sourceUrl = "https://bit.ly/quick-cash"
        )

        assertTrue(scamCheck.isWarning)
        assertTrue(scamCheck.explanation.contains("registration, training, or deposit fee"))
    }
}
