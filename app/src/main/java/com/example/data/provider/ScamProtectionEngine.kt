package com.example.data.provider

import com.example.data.model.Job

object ScamProtectionEngine {

    data class ScamCheckResult(
        val isWarning: Boolean,
        val explanation: String
    )

    /**
     * Inspects a job listing for known red flags in the Indian employment market.
     * Returns whether the job needs review, and a clear, objective explanation.
     */
    fun evaluateJob(
        title: String,
        company: String,
        description: String,
        salaryLpa: Double,
        minExpYears: Int,
        sourceUrl: String
    ): ScamCheckResult {
        val lowerDesc = description.lowercase()
        val lowerTitle = title.lowercase()
        val reasons = mutableListOf<String>()

        // 1. Demanding upfront payments / security deposit / training fee
        if (lowerDesc.contains("security deposit") || lowerDesc.contains("registration fee") ||
            lowerDesc.contains("training fee") || lowerDesc.contains("refundable deposit") ||
            lowerDesc.contains("pay to apply") || lowerDesc.contains("processing fee")
        ) {
            reasons.add("Mentions registration, training, or deposit fee (legitimate employers never charge application fees)")
        }

        // 2. WhatsApp or Telegram only contact without corporate domain
        if ((lowerDesc.contains("whatsapp only") || lowerDesc.contains("telegram") || lowerDesc.contains("chat on whatsapp")) &&
            (lowerDesc.contains("@gmail.com") || lowerDesc.contains("@yahoo.com") || lowerDesc.contains("@hotmail.com"))
        ) {
            reasons.add("Recruiter provides only personal messaging (WhatsApp/Telegram) and free webmail rather than an official corporate email")
        }

        // 3. Demanding banking / OTP / sensitive financial information
        if (lowerDesc.contains("bank account details") || lowerDesc.contains("share otp") || lowerDesc.contains("upi pin")) {
            reasons.add("Requests sensitive financial details (OTP/UPI) during preliminary stages")
        }

        // 4. Unrealistic salary for freshers/data entry
        if ((lowerTitle.contains("data entry") || lowerTitle.contains("sms sending") || lowerTitle.contains("typing job")) &&
            (salaryLpa > 8.0 || lowerDesc.contains("50,000 per week") || lowerDesc.contains("10,000 daily"))
        ) {
            reasons.add("Salary offered appears unusually high for entry-level typing or clerical tasks without verified corporate verification")
        }

        // 5. Suspicious shortened URLs
        if (sourceUrl.contains("bit.ly") || sourceUrl.contains("tinyurl.com") || sourceUrl.contains("t.me")) {
            reasons.add("Official destination links through generic URL shorteners rather than employer career domain")
        }

        return if (reasons.isNotEmpty()) {
            ScamCheckResult(
                isWarning = true,
                explanation = reasons.joinToString(" • ")
            )
        } else {
            ScamCheckResult(
                isWarning = false,
                explanation = ""
            )
        }
    }
}
