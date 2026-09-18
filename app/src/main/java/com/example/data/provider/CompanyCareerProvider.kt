package com.example.data.provider

import com.example.data.model.Job
import com.example.data.model.SourceClassification
import com.example.data.model.WorkType

class CompanyCareerProvider : JobSourceProvider {
    override val providerId: String = "company_ats_direct"
    override val providerDisplayName: String = "Company Career Portals (ATS)"
    override val classification: SourceClassification = SourceClassification.EMPLOYER_CAREER_INTEGRATION
    override val isDirectApiAuthorized: Boolean = true

    override suspend fun fetchJobs(query: String?, location: String?): List<Job> {
        return listOf(
            Job(
                id = "ats_zoho_001",
                title = "Senior Android Developer",
                company = "Zoho Corporation",
                locationCity = "Chennai",
                locationState = "Tamil Nadu",
                workType = WorkType.ON_SITE,
                minExpYears = 3,
                maxExpYears = 6,
                minSalaryLpa = 12.0,
                maxSalaryLpa = 20.0,
                salaryMonthlyInr = 140000,
                description = "Join Zoho's core mobile engineering team building high-scale SaaS productivity apps used by 100M+ global users. You will architect robust offline-first Android components using Kotlin and modern Android SDKs.",
                responsibilities = listOf(
                    "Architect and implement modern Jetpack Compose UI modules",
                    "Optimize local SQLite/Room caching and synchronization engines",
                    "Collaborate with backend teams to design low-latency REST and WebSocket APIs",
                    "Mentor junior Android engineers and conduct code reviews"
                ),
                requirements = listOf(
                    "3+ years of native Android application development experience",
                    "Strong proficiency in Kotlin, Coroutines, Flow, and Jetpack Compose",
                    "Deep knowledge of Android lifecycle, memory optimization, and offline sync",
                    "B.E / B.Tech in Computer Science or equivalent practical experience"
                ),
                skillsRequired = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Room", "REST API", "Git", "Performance Optimization"),
                educationRequired = listOf("B.E.", "B.Tech", "MCA", "M.Sc Computer Science"),
                benefits = listOf("Free campus meals & transport", "Health Insurance for family", "Continuous learning allowance", "Annual performance bonus"),
                sourceName = "Zoho Careers (Direct)",
                sourceUrl = "https://www.zoho.com/careers/chennai-android-dev",
                sourceClassification = SourceClassification.EMPLOYER_CAREER_INTEGRATION,
                isOfficialEmployerPage = true,
                postedTimeAgo = "3h ago",
                isGovernment = false,
                isFresherEligible = false,
                isItRole = true,
                industry = "SaaS / Technology"
            ),
            Job(
                id = "ats_mmt_002",
                title = "Tour Operations & Customer Experience Executive",
                company = "MakeMyTrip India",
                locationCity = "Chennai",
                locationState = "Tamil Nadu",
                workType = WorkType.HYBRID,
                minExpYears = 1,
                maxExpYears = 4,
                minSalaryLpa = 4.5,
                maxSalaryLpa = 7.0,
                salaryMonthlyInr = 45000,
                description = "MakeMyTrip Holidays is hiring an Operations Executive in Chennai to coordinate premium domestic and international holiday bookings, manage vendor/hotel relations, and resolve traveler queries swiftly.",
                responsibilities = listOf(
                    "Manage end-to-end holiday itinerary confirmations and voucher issuance",
                    "Coordinate with local destination management companies and hotel partners",
                    "Address customer inquiries via phone, email, and live CRM support",
                    "Ensure seamless guest experiences and resolve travel disruptions"
                ),
                requirements = listOf(
                    "1-4 years of experience in Tour Operations, Travel Desk, or Hospitality",
                    "Excellent verbal and written communication in English and Tamil",
                    "Strong problem-solving skills and familiarity with CRM and reservation portals",
                    "Bachelor's degree in Tourism, Computer Science, Arts, or Commerce"
                ),
                skillsRequired = listOf("Customer Handling", "Tour Operations", "Vendor Coordination", "Reservation Systems", "MS Excel", "English Communication"),
                educationRequired = listOf("B.Sc", "B.A.", "B.Com", "BBA", "Diploma in Tourism"),
                benefits = listOf("Travel discounts", "Medical Cover ₹5L", "Quarterly incentives", "Hybrid 2-day work from home"),
                sourceName = "MakeMyTrip Careers (Lever ATS)",
                sourceUrl = "https://jobs.lever.co/makemytrip/chennai-operations-exec",
                sourceClassification = SourceClassification.EMPLOYER_CAREER_INTEGRATION,
                isOfficialEmployerPage = true,
                postedTimeAgo = "5h ago",
                isGovernment = false,
                isFresherEligible = false,
                isItRole = false,
                industry = "Travel & Tourism"
            ),
            Job(
                id = "ats_hdfc_003",
                title = "Assistant Branch Operations & Accounts Officer",
                company = "HDFC Bank",
                locationCity = "Coimbatore",
                locationState = "Tamil Nadu",
                workType = WorkType.ON_SITE,
                minExpYears = 0,
                maxExpYears = 3,
                minSalaryLpa = 3.8,
                maxSalaryLpa = 5.5,
                salaryMonthlyInr = 35000,
                description = "HDFC Bank is hiring enthusiastic banking operations executives to manage customer branch transactions, account reconciliations, regulatory compliance, and digital banking onboarding.",
                responsibilities = listOf(
                    "Facilitate customer banking transactions and KYC documentation verification",
                    "Perform daily cash balance reconciliation and ledger entries",
                    "Assist customers with net banking, mobile banking, and loan documentation",
                    "Adhere strictly to RBI compliance and branch audit protocols"
                ),
                requirements = listOf(
                    "0 to 3 years experience (Freshers with B.Com/BBA/B.Sc welcome)",
                    "Basic computer operations and spreadsheet numeracy",
                    "Customer-friendly disposition and integrity",
                    "Local language fluency (Tamil) and working English"
                ),
                skillsRequired = listOf("Banking Operations", "KYC Verification", "Tally/Excel", "Customer Relations", "Reconciliation"),
                educationRequired = listOf("B.Com", "BBA", "B.Sc", "B.A.", "M.Com"),
                benefits = listOf("Banking allowances", "Pension/PF", "Medical Insurance", "Fast-track promotional exams"),
                sourceName = "HDFC Bank Careers (Direct)",
                sourceUrl = "https://www.hdfcbank.com/careers/coimbatore-ops",
                sourceClassification = SourceClassification.EMPLOYER_CAREER_INTEGRATION,
                isOfficialEmployerPage = true,
                postedTimeAgo = "Today",
                isGovernment = false,
                isFresherEligible = true,
                isItRole = false,
                industry = "Banking & Finance"
            ),
            Job(
                id = "ats_freshworks_004",
                title = "Customer Support Specialist (Global Voice & Non-Voice)",
                company = "Freshworks",
                locationCity = "Chennai",
                locationState = "Tamil Nadu",
                workType = WorkType.HYBRID,
                minExpYears = 0,
                maxExpYears = 2,
                minSalaryLpa = 4.0,
                maxSalaryLpa = 6.0,
                salaryMonthlyInr = 40000,
                description = "Be the face and voice of Freshworks for global enterprise clients. You will assist users via chat, email, and call tickets, troubleshoot product configurations, and advocate for customer satisfaction.",
                responsibilities = listOf(
                    "Respond to inbound support tickets and live chats within SLA targets",
                    "Diagnose technical glitches and coordinate with product development squads",
                    "Document solutions in internal knowledge base articles",
                    "Collect customer feedback and champion product enhancements"
                ),
                requirements = listOf(
                    "0-2 years experience in Customer Support, IT Helpdesk, or BPO",
                    "Superb written and spoken English communication skills",
                    "Empathetic mindset with high patience and analytical problem solving",
                    "Comfortable with rotational shift allowance"
                ),
                skillsRequired = listOf("Customer Support", "Zendesk/Freshdesk", "Ticket Resolution", "English Fluency", "Analytical Troubleshooting"),
                educationRequired = listOf("Any Graduate", "B.Sc", "B.A.", "B.Com", "BCA", "B.E."),
                benefits = listOf("Cab pick-up & drop", "Shift allowances ₹6000/mo", "Stock options (ESOPs)", "Full health insurance"),
                sourceName = "Freshworks Careers (Greenhouse ATS)",
                sourceUrl = "https://boards.greenhouse.io/freshworks/support-specialist",
                sourceClassification = SourceClassification.EMPLOYER_CAREER_INTEGRATION,
                isOfficialEmployerPage = true,
                postedTimeAgo = "1d ago",
                isGovernment = false,
                isFresherEligible = true,
                isItRole = false,
                industry = "SaaS / Customer Experience"
            )
        )
    }
}
