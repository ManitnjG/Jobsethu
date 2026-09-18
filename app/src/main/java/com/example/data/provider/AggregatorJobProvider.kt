package com.example.data.provider

import com.example.data.model.Job
import com.example.data.model.JobDuplicateSource
import com.example.data.model.SourceClassification
import com.example.data.model.WorkType

class AggregatorJobProvider : JobSourceProvider {
    override val providerId: String = "indian_job_aggregators"
    override val providerDisplayName: String = "Indian & Global Aggregator Feeds"
    override val classification: SourceClassification = SourceClassification.PUBLIC_PERMITTED_FEED
    override val isDirectApiAuthorized: Boolean = false

    override suspend fun fetchJobs(query: String?, location: String?): List<Job> {
        return listOf(
            Job(
                id = "agg_naukri_101",
                title = "Tour Operations & Travel Consultant",
                company = "MakeMyTrip India",
                locationCity = "Chennai",
                locationState = "Tamil Nadu",
                workType = WorkType.HYBRID,
                minExpYears = 2,
                maxExpYears = 4,
                minSalaryLpa = 4.5,
                maxSalaryLpa = 6.8,
                salaryMonthlyInr = 45000,
                description = "Experienced operations executive required for holiday bookings, customer handling, vendor coordination, and reservation assistance.",
                responsibilities = listOf("Booking tickets and hotels", "Guest assistance", "Vendor coordination"),
                requirements = listOf("Experience in tourism operations", "Good communication"),
                skillsRequired = listOf("Customer Handling", "Tour Operations", "Vendor Coordination", "Reservation Systems", "MS Excel"),
                educationRequired = listOf("B.Sc", "B.Com", "B.A."),
                benefits = listOf("Travel vouchers", "Health Insurance"),
                sourceName = "Naukri",
                sourceUrl = "https://www.naukri.com/job-listings-tour-operations-mmt-chennai",
                sourceClassification = SourceClassification.EXTERNAL_APPLICATION,
                isOfficialEmployerPage = false,
                postedTimeAgo = "6h ago",
                isGovernment = false,
                isFresherEligible = false,
                isItRole = false,
                industry = "Travel & Tourism"
            ),
            Job(
                id = "agg_linkedin_102",
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
                description = "Zoho is hiring Senior Android Developer in Chennai for enterprise mobile applications with Kotlin, Compose and Coroutines.",
                responsibilities = listOf("Build Compose screens", "Performance tuning"),
                requirements = listOf("3+ years Android native"),
                skillsRequired = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Room"),
                educationRequired = listOf("B.Tech", "MCA"),
                benefits = listOf("Meals provided", "Insurance"),
                sourceName = "LinkedIn Jobs",
                sourceUrl = "https://www.linkedin.com/jobs/view/zoho-android-developer",
                sourceClassification = SourceClassification.EXTERNAL_APPLICATION,
                isOfficialEmployerPage = false,
                postedTimeAgo = "5h ago",
                isGovernment = false,
                isFresherEligible = false,
                isItRole = true,
                industry = "SaaS / Technology"
            ),
            Job(
                id = "agg_internshala_103",
                title = "Junior Software Engineer (Fresher / Trainee)",
                company = "Infosys Limited",
                locationCity = "Bengaluru",
                locationState = "Karnataka",
                workType = WorkType.HYBRID,
                minExpYears = 0,
                maxExpYears = 1,
                minSalaryLpa = 3.6,
                maxSalaryLpa = 4.5,
                salaryMonthlyInr = 30000,
                description = "Infosys is hiring fresh graduates for Systems Engineer / Junior Software roles. Training provided in Mysore campus in Full Stack, Cloud, and Java/Python.",
                responsibilities = listOf(
                    "Participate in foundational software training program",
                    "Develop and test enterprise software modules under mentor supervision",
                    "Participate in daily agile standups and code reviews"
                ),
                requirements = listOf(
                    "2023 or 2024 passout with B.E., B.Tech, MCA, or B.Sc (Computer Science/IT)",
                    "Minimum 60% aggregate across 10th, 12th, and graduation",
                    "Foundational understanding of OOP concepts and basic algorithms"
                ),
                skillsRequired = listOf("Java", "Python", "SQL", "Data Structures", "Problem Solving"),
                educationRequired = listOf("B.E.", "B.Tech", "MCA", "B.Sc Computer Science"),
                benefits = listOf("Comprehensive world-class Mysore training", "Group health cover", "Gratuity and PF"),
                sourceName = "Freshersworld / Direct",
                sourceUrl = "https://career.infosys.com/jobdesc?jobId=10923",
                sourceClassification = SourceClassification.EMPLOYER_CAREER_INTEGRATION,
                isOfficialEmployerPage = true,
                postedTimeAgo = "Today",
                isGovernment = false,
                isFresherEligible = true,
                isItRole = true,
                industry = "IT Services"
            ),
            Job(
                id = "agg_indeed_104",
                title = "Remote Data Analyst & MIS Executive",
                company = "Swiggy India",
                locationCity = "Bengaluru",
                locationState = "Karnataka",
                workType = WorkType.REMOTE,
                minExpYears = 1,
                maxExpYears = 3,
                minSalaryLpa = 5.5,
                maxSalaryLpa = 8.5,
                salaryMonthlyInr = 55000,
                description = "Work from anywhere in India as a Data Analyst for Swiggy Delivery & Partner Operations. Build automated dashboards, run SQL queries, and derive partner insights.",
                responsibilities = listOf(
                    "Write optimized SQL queries to extract multi-city order metrics",
                    "Build executive dashboards in Power BI and Google Data Studio",
                    "Perform ad-hoc root-cause analysis on delivery partner delivery times"
                ),
                requirements = listOf(
                    "1-3 years experience in Data Analytics or Business Intelligence",
                    "Expertise in SQL, Advanced Excel (VLOOKUP, Pivot, Macros), and Power BI",
                    "Comfortable with fast-paced metrics and remote cross-functional teamwork"
                ),
                skillsRequired = listOf("SQL", "Advanced Excel", "Power BI", "Data Analytics", "Python", "Problem Solving"),
                educationRequired = listOf("B.Sc", "B.Com", "B.E.", "BCA", "Statistics"),
                benefits = listOf("100% Work from Home", "Home office setup allowance ₹20,000", "Swiggy One corporate perks"),
                sourceName = "Indeed India",
                sourceUrl = "https://in.indeed.com/viewjob?jk=swiggy-remote-data-analyst",
                sourceClassification = SourceClassification.EXTERNAL_APPLICATION,
                isOfficialEmployerPage = false,
                postedTimeAgo = "1d ago",
                isGovernment = false,
                isFresherEligible = false,
                isItRole = true,
                industry = "E-Commerce / Food Delivery"
            ),
            Job(
                id = "agg_apna_105",
                title = "Operations & Logistics Coordinator",
                company = "Delhivery",
                locationCity = "Madurai",
                locationState = "Tamil Nadu",
                workType = WorkType.ON_SITE,
                minExpYears = 0,
                maxExpYears = 2,
                minSalaryLpa = 2.8,
                maxSalaryLpa = 3.8,
                salaryMonthlyInr = 25000,
                description = "Manage parcel hub dispatch, fleet routing, and delivery partner coordination for South Tamil Nadu distribution hub.",
                responsibilities = listOf(
                    "Coordinate incoming linehaul trailers and sorting schedules",
                    "Allocate delivery routes to 50+ field delivery executives",
                    "Track real-time delivery escalations and manage RTO (return-to-origin) audit"
                ),
                requirements = listOf(
                    "Diploma, 12th, or Bachelor's degree (B.Com/B.Sc/B.A./BBA)",
                    "Fluency in Tamil and basic smartphone / portal operational skills",
                    "Immediate joiners preferred"
                ),
                skillsRequired = listOf("Logistics Coordination", "Dispatch Management", "Excel", "Vendor Coordination", "Field Ops"),
                educationRequired = listOf("12th", "Diploma", "B.A.", "B.Com", "B.Sc"),
                benefits = listOf("PF + ESI benefits", "Performance incentive up to ₹3000/month"),
                sourceName = "Apna",
                sourceUrl = "https://apna.co/job/delhivery-hub-ops-madurai",
                sourceClassification = SourceClassification.EXTERNAL_APPLICATION,
                isOfficialEmployerPage = false,
                postedTimeAgo = "2h ago",
                isGovernment = false,
                isFresherEligible = true,
                isItRole = false,
                industry = "Logistics & Supply Chain"
            ),
            Job(
                id = "agg_scam_flagged_106",
                title = "Online Data Entry & SMS Processing Executive (Immediate)",
                company = "Global Tech Work Solutions",
                locationCity = "Chennai",
                locationState = "Tamil Nadu",
                workType = WorkType.REMOTE,
                minExpYears = 0,
                maxExpYears = 1,
                minSalaryLpa = 9.5,
                maxSalaryLpa = 14.0,
                salaryMonthlyInr = 90000,
                description = "Earn ₹50,000 per week from home doing simple SMS sending and copy paste. Registration fee ₹1,500 refundable deposit required before starting work. WhatsApp only: 9876543210 (recruiter@gmail.com). No interviews, immediate start!",
                responsibilities = listOf("Copy text and send SMS from home"),
                requirements = listOf("Any 10th/12th pass", "Smartphone"),
                skillsRequired = listOf("Typing", "Data Entry"),
                educationRequired = listOf("10th", "12th"),
                benefits = listOf("Earn ₹10,000 daily"),
                sourceName = "Unverified Classifieds",
                sourceUrl = "https://bit.ly/quick-sms-job-chennai",
                sourceClassification = SourceClassification.EXTERNAL_APPLICATION,
                isOfficialEmployerPage = false,
                postedTimeAgo = "1h ago",
                isGovernment = false,
                isFresherEligible = true,
                isItRole = false,
                industry = "Unverified / General",
                isScamWarning = true,
                scamWarningReason = "Mentions registration fee/refundable deposit • Recruiter provides only WhatsApp and free webmail (@gmail.com) • Unrealistic salary (₹14 LPA for basic typing) • Destination links via shortened bit.ly URL"
            )
        )
    }
}
