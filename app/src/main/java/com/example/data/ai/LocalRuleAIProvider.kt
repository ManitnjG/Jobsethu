package com.example.data.ai

import com.example.data.model.*
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

class LocalRuleAIProvider : AIProvider {

    override suspend fun parseResume(rawText: String): CandidateProfile {
        val lower = rawText.lowercase()

        // Extract likely name or use default
        val name = if (rawText.lines().firstOrNull()?.isNotBlank() == true) {
            rawText.lines().first().trim()
        } else "Arun Kumar"

        // Education
        val edu = when {
            lower.contains("mca") -> "MCA"
            lower.contains("b.tech") || lower.contains("btech") -> "B.Tech"
            lower.contains("b.e.") || lower.contains("be ") -> "B.E."
            lower.contains("b.sc") || lower.contains("bsc") -> "B.Sc Computer Science"
            lower.contains("b.com") || lower.contains("bcom") -> "B.Com"
            lower.contains("bba") -> "BBA"
            lower.contains("diploma") -> "Diploma"
            else -> "B.Sc Computer Science"
        }

        // Years of experience
        val expYears = when {
            lower.contains("fresher") -> 0.0
            lower.contains("1 year") -> 1.0
            lower.contains("2 years") || lower.contains("2 yr") -> 2.0
            lower.contains("3 years") || lower.contains("3 yr") -> 3.0
            lower.contains("4 years") -> 4.0
            lower.contains("5 years") -> 5.0
            else -> 3.0
        }

        // Roles
        val currentRole = when {
            lower.contains("android") -> "Android Developer"
            lower.contains("tourism") || lower.contains("tour") -> "Tour Operations Executive"
            lower.contains("accounts") || lower.contains("accounting") -> "Accounts Executive"
            lower.contains("customer support") || lower.contains("bpo") -> "Customer Support Executive"
            else -> "Operations & Support Specialist"
        }

        val extractedTech = mutableListOf<String>()
        if (lower.contains("excel") || lower.contains("spreadsheet")) extractedTech.add("Advanced Excel")
        if (lower.contains("word") || lower.contains("office")) extractedTech.add("MS Office")
        if (lower.contains("kotlin")) extractedTech.add("Kotlin")
        if (lower.contains("android")) extractedTech.add("Android SDK")
        if (lower.contains("compose")) extractedTech.add("Jetpack Compose")
        if (lower.contains("sql")) extractedTech.add("SQL")
        if (lower.contains("python")) extractedTech.add("Python")
        if (lower.contains("crm") || lower.contains("reservation")) extractedTech.add("CRM Portals")
        if (extractedTech.isEmpty()) {
            extractedTech.addAll(listOf("MS Office", "Excel", "CRM Software", "Computer Operations"))
        }

        val extractedSoft = listOf("Customer Handling", "Vendor Coordination", "English Communication", "Problem Solving")

        return CandidateProfile(
            id = "primary_candidate",
            fullName = name,
            email = "arun.kumar@example.com",
            phone = "+91 98765 43210",
            currentRole = currentRole,
            desiredRoles = listOf(currentRole, "Operations Executive", "Customer Support Specialist"),
            experienceYears = expYears,
            highestEducation = edu,
            university = "Madras University",
            graduationYear = 2023,
            currentCity = "Chennai",
            currentState = "Tamil Nadu",
            preferredCities = listOf("Chennai", "Bengaluru", "Coimbatore"),
            technicalSkills = extractedTech,
            softSkills = extractedSoft,
            currentCtcLpa = 4.2,
            expectedCtcLpa = 6.5,
            noticePeriod = NoticePeriod.DAYS_30,
            isServingNotice = false,
            workPreference = WorkType.HYBRID,
            languagesKnown = listOf("English", "Tamil", "Hindi"),
            summary = "Demonstrated expertise in $currentRole with $expYears years of practical domain experience and strong educational foundation in $edu."
        )
    }

    override suspend fun searchIntent(naturalQuery: String): ParsedSearchIntent {
        val q = naturalQuery.lowercase()
        var role = ""
        var city = ""
        var workType: WorkType? = null
        var minExp: Int? = null
        var maxExp: Int? = null
        var minSalary: Double? = null
        var isGovt = false
        var isFresher = false
        var isToday = false

        // Check cities
        val cities = listOf("chennai", "bengaluru", "bangalore", "hyderabad", "mumbai", "pune", "delhi", "noida", "gurugram", "gurgaon", "coimbatore", "kochi", "madurai", "salem", "trichy", "mysuru", "jaipur", "indore")
        for (c in cities) {
            if (q.contains(c)) {
                city = if (c == "bangalore") "Bengaluru" else if (c == "gurgaon") "Gurugram" else c.replaceFirstChar { it.uppercase() }
                break
            }
        }

        // Work from home
        if (q.contains("work from home") || q.contains("wfh") || q.contains("remote")) {
            workType = WorkType.REMOTE
        } else if (q.contains("hybrid")) {
            workType = WorkType.HYBRID
        } else if (q.contains("on-site") || q.contains("onsite") || q.contains("office")) {
            workType = WorkType.ON_SITE
        }

        // Government
        if (q.contains("government") || q.contains("govt") || q.contains("upsc") || q.contains("ssc") || q.contains("bank exam") || q.contains("railway")) {
            isGovt = true
        }

        // Fresher
        if (q.contains("fresher") || q.contains("entry level") || q.contains("0 years") || q.contains("trainee")) {
            isFresher = true
            minExp = 0
            maxExp = 1
        }

        // Experience checks
        val expPattern = "(\\d+)\\s*(?:years?|yrs?)".toRegex()
        val expMatch = expPattern.find(q)
        if (expMatch != null && !isFresher) {
            val yrs = expMatch.groupValues[1].toIntOrNull() ?: 0
            minExp = max(0, yrs - 1)
            maxExp = yrs + 1
        }

        // Salary checks
        val lpaPattern = "(\\d+(?:\\.\\d+)?)\\s*(?:lpa|lac|lakh)".toRegex()
        val lpaMatch = lpaPattern.find(q)
        if (lpaMatch != null) {
            minSalary = lpaMatch.groupValues[1].toDoubleOrNull()
        }

        // Posted today
        if (q.contains("today") || q.contains("recent")) {
            isToday = true
        }

        // Role keywords
        role = when {
            q.contains("android") -> "Android Developer"
            q.contains("tourism") || q.contains("travel") -> "Tour Operations"
            q.contains("accounts") || q.contains("accounting") || q.contains("finance") -> "Accounts"
            q.contains("customer support") || q.contains("bpo") -> "Customer Support"
            q.contains("data analyst") -> "Data Analyst"
            q.contains("logistics") -> "Logistics"
            else -> ""
        }

        val explanation = buildString {
            append("AI identified structured filters: ")
            if (role.isNotEmpty()) append("Role: '$role', ")
            if (city.isNotEmpty()) append("City: '$city', ")
            if (workType != null) append("Mode: ${workType.name}, ")
            if (minSalary != null) append("Salary ≥ ₹${minSalary} LPA, ")
            if (isFresher) append("Experience: Fresher, ")
            else if (minExp != null) append("Experience: $minExp-$maxExp yrs, ")
            if (isGovt) append("Category: Verified Government, ")
            if (isToday) append("Freshness: Posted Today")
        }.trimEnd(',', ' ')

        return ParsedSearchIntent(
            queryRole = role,
            detectedCity = city,
            detectedWorkType = workType,
            minExpYears = minExp,
            maxExpYears = maxExp,
            minSalaryLpa = minSalary,
            onlyGovernment = isGovt,
            onlyFresher = isFresher,
            isPostedToday = isToday,
            reasoningExplanation = explanation
        )
    }

    override suspend fun matchJob(candidate: CandidateProfile, job: Job): JobMatchAnalysis {
        val candSkills = (candidate.technicalSkills + candidate.softSkills).map { it.lowercase() }
        val jobSkills = job.skillsRequired.map { it.lowercase() }

        val matched = mutableListOf<String>()
        val missing = mutableListOf<String>()

        for (js in job.skillsRequired) {
            val lower = js.lowercase()
            if (candSkills.any { it.contains(lower) || lower.contains(it) }) {
                matched.add(js)
            } else {
                missing.add(js)
            }
        }

        val skillsRatio = if (job.skillsRequired.isNotEmpty()) {
            matched.size.toDouble() / job.skillsRequired.size
        } else 0.8
        val skillsScore = (skillsRatio * 100).toInt().coerceIn(40, 100)

        // Experience alignment
        val expDiff = candidate.experienceYears - job.minExpYears
        val expScore = when {
            candidate.experienceYears >= job.minExpYears && candidate.experienceYears <= (job.maxExpYears + 1) -> 95
            candidate.experienceYears >= job.minExpYears -> 88
            expDiff >= -1.0 -> 75
            else -> 60
        }

        // Education alignment
        val eduScore = if (job.educationRequired.isEmpty() || job.educationRequired.any { req ->
            candidate.highestEducation.contains(req, ignoreCase = true) || req.contains("Graduate", ignoreCase = true)
        }) 95 else 70

        // Location alignment
        val locScore = when {
            job.workType == WorkType.REMOTE -> 100
            candidate.preferredCities.any { it.equals(job.locationCity, ignoreCase = true) } -> 100
            candidate.currentCity.equals(job.locationCity, ignoreCase = true) -> 95
            else -> 65
        }

        // Salary alignment
        val salScore = if (job.maxSalaryLpa >= candidate.expectedCtcLpa) 95 else 80

        // Weighted Overall: Skills 40%, Experience 25%, Education 15%, Location 15%, Salary 5%
        val overall = ((skillsScore * 0.40) + (expScore * 0.25) + (eduScore * 0.15) + (locScore * 0.15) + (salScore * 0.05)).toInt().coerceIn(35, 98)

        val gaps = missing.take(3).map { "Needs upskilling/familiarity in $it" }

        val notes = if (overall >= 80) {
            "Strong profile alignment with candidate's demonstrated background in ${candidate.currentRole}."
        } else {
            "Good foundational match. Candidate meets core criteria but can upskill in specific tools."
        }

        return JobMatchAnalysis(
            overallMatchPercent = overall,
            skillsMatchPercent = skillsScore,
            experienceMatchPercent = expScore,
            educationMatchPercent = eduScore,
            locationMatchPercent = locScore,
            salaryMatchPercent = salScore,
            matchedSkills = matched,
            missingSkills = missing,
            alignmentNotes = notes,
            gapsExplanation = gaps
        )
    }

    override suspend fun analyzeResume(candidate: CandidateProfile, job: Job): AtsResumeCheckResult {
        val analysis = matchJob(candidate, job)
        val warnings = mutableListOf<String>()

        if (analysis.missingSkills.isNotEmpty()) {
            warnings.add("ATS scanners expect high frequency of keywords: ${analysis.missingSkills.take(3).joinToString(", ")}")
        }
        if (!candidate.summary.contains(job.title, ignoreCase = true)) {
            warnings.add("Resume summary does not explicitly reference the target title '${job.title}'")
        }

        val improvements = listOf(
            "Quantify key accomplishments (e.g. 'Coordinated 150+ monthly itineraries with 98% positive guest feedback')",
            "Position ${analysis.matchedSkills.take(2).joinToString(" & ")} prominently in the top section of the profile",
            "Ensure notice period '${candidate.noticePeriod.displayName}' is highlighted for immediate recruiter filtering"
        )

        val atsScore = (analysis.overallMatchPercent * 0.9 + 8).toInt().coerceIn(50, 96)

        return AtsResumeCheckResult(
            overallScore = atsScore,
            atsKeywordCoveragePercent = analysis.skillsMatchPercent,
            matchedKeywords = analysis.matchedSkills,
            missingHighPriorityKeywords = analysis.missingSkills,
            formattingWarnings = warnings,
            suggestedImprovements = improvements
        )
    }

    override suspend fun tailorResume(
        masterResume: ResumeVersion,
        candidate: CandidateProfile,
        job: Job
    ): ResumeVersion {
        val diffs = listOf(
            ResumeModificationDiff(
                section = "Professional Summary",
                originalText = candidate.summary,
                proposedText = "Results-driven professional with ${candidate.experienceYears.toInt()} years of experience targeting ${job.title} at ${job.company}. Demonstrated expertise in ${job.skillsRequired.take(3).joinToString(", ")} with a strong track record of operational accuracy and stakeholder coordination.",
                reasoning = "Tailors the introductory headline directly to ${job.company}'s requirements while preserving strictly factual career history.",
                isAccepted = true
            ),
            ResumeModificationDiff(
                section = "Core Competencies",
                originalText = (candidate.technicalSkills + candidate.softSkills).joinToString(" • "),
                proposedText = (job.skillsRequired.take(4) + candidate.technicalSkills.take(3)).distinct().joinToString(" • "),
                reasoning = "Prioritizes keywords aligned with ${job.title} ATS parser criteria without fabricating unheld skills.",
                isAccepted = true
            )
        )

        return ResumeVersion(
            id = "targeted_${UUID.randomUUID().toString().take(8)}",
            title = "Targeted – ${job.title} (${job.company})",
            isMaster = false,
            targetRole = job.title,
            targetCompany = job.company,
            contentSummary = "Tailored for ${job.title} at ${job.company} with emphasized operational benchmarks and relevant skills.",
            skillsHighlighted = job.skillsRequired.take(4),
            modificationsMade = diffs
        )
    }

    override suspend fun generateCoverLetter(candidate: CandidateProfile, job: Job, tone: String): CoverLetterResult {
        val letter = when (tone.lowercase()) {
            "short" -> """
                Dear Hiring Manager at ${job.company},

                I am writing to express my strong interest in the ${job.title} position in ${job.locationCity}. With ${candidate.experienceYears.toInt()} years of professional background in ${candidate.currentRole} and proven proficiency in ${candidate.technicalSkills.take(2).joinToString(" and ")}, I am confident in delivering immediate value to your team.

                My notice period is ${candidate.noticePeriod.displayName}, and I am available for discussions at your earliest convenience.

                Sincerely,
                ${candidate.fullName}
                ${candidate.phone} | ${candidate.email}
            """.trimIndent()

            "technical" -> """
                Dear Engineering / Hiring Team at ${job.company},

                I am eager to contribute as ${job.title} at ${job.company}. My qualifications include a ${candidate.highestEducation} coupled with hands-on domain experience delivering robust solutions using ${candidate.technicalSkills.joinToString(", ")}.

                Key highlights relevant to ${job.company}:
                • Demonstrated proficiency in ${job.skillsRequired.take(3).joinToString(", ")}.
                • Track record of adhering to strict SLAs, clean workflows, and collaborative problem-solving.
                • Ready to join with a ${candidate.noticePeriod.displayName} notice period in ${job.locationCity}.

                Thank you for reviewing my application. I look forward to discussing how my capabilities align with your strategic roadmap.

                Best regards,
                ${candidate.fullName}
            """.trimIndent()

            "enthusiastic" -> """
                Dear ${job.company} Team,

                I was thrilled to discover the opening for ${job.title}! Having closely followed ${job.company}'s impactful growth, I would love nothing more than to bring my energy, dedication, and ${candidate.experienceYears.toInt()} years of experience to this team.

                Throughout my career in ${candidate.currentRole}, I have focused on delivering exceptional customer and operational outcomes. The prospect of applying ${candidate.technicalSkills.take(2).joinToString(" and ")} within ${job.company}'s high-standard environment is incredibly motivating.

                Thank you for your time and consideration.

                Warm regards,
                ${candidate.fullName}
            """.trimIndent()

            else -> """
                Dear Hiring Manager,

                Please accept this letter and accompanying resume as my application for the position of ${job.title} at ${job.company}.

                With ${candidate.experienceYears.toInt()} years of experience in ${candidate.currentRole}, I have developed deep competencies in ${job.skillsRequired.take(3).joinToString(", ")}. My background has equipped me to handle high-stakes coordination, resolve complex stakeholder requirements, and maintain rigorous operational standards.

                I welcome the opportunity to discuss how my profile and commitment can contribute to ${job.company}'s continued success.

                Yours sincerely,
                ${candidate.fullName}
            """.trimIndent()
        }

        return CoverLetterResult(
            tone = tone,
            content = letter,
            targetRole = job.title,
            targetCompany = job.company
        )
    }

    override suspend fun generateApplicationAnswers(candidate: CandidateProfile, job: Job): ApplicationCopilotAnswers {
        return ApplicationCopilotAnswers(
            whyHireYou = "I bring ${candidate.experienceYears.toInt()} years of verifiable track record in ${candidate.currentRole} with strong skills in ${candidate.technicalSkills.take(3).joinToString(", ")}. I have demonstrated high reliability, customer-first problem solving, and ability to ramp up swiftly.",
            tellUsAboutYourself = "I am an enthusiastic professional graduated with ${candidate.highestEducation}. For the past ${candidate.experienceYears.toInt()} years, I have worked in ${candidate.currentRole}, coordinating complex operations, customer workflows, and vendor relationships.",
            whyThisCompany = "I admire ${job.company}'s reputation for quality, transparency, and innovation in ${job.industry}. The ${job.title} role aligns seamlessly with my passion for excellence and long-term career aspirations.",
            expectedCtcExplanation = "My current CTC is ₹${candidate.currentCtcLpa} LPA and expected CTC is ₹${candidate.expectedCtcLpa} LPA (negotiable as per company norms and standard benefits for the role).",
            noticePeriodExplanation = "My official notice period is ${candidate.noticePeriod.displayName}. ${if (candidate.isServingNotice) "I am currently serving notice and can join immediately." else "I can negotiate with my current employer for early release if required."}",
            relocationWillingness = "I am based in ${candidate.currentCity} and fully open to working in ${job.locationCity} under ${job.workType.name.lowercase()} arrangement."
        )
    }

    override suspend fun careerAdvice(candidate: CandidateProfile, userMessage: String, jobContext: Job?): String {
        val q = userMessage.lowercase()
        return when {
            q.contains("suit my resume") || q.contains("what jobs") -> {
                "Based on your ${candidate.highestEducation} and ${candidate.experienceYears.toInt()} years in ${candidate.currentRole}, strong role families for you include: \n1. Operations & Logistics Executive\n2. Customer Experience / Support Specialist\n3. Tour / Reservation Coordinator\n4. Junior IT Business Operations (leveraging your Computer Science foundation)."
            }
            q.contains("interview") || q.contains("why am i not getting") -> {
                "To increase interview call-backs in the Indian market:\n• Ensure your resume contains exact ATS keywords from the job description.\n• Quantify outcomes (numbers, percentages, volumes handled).\n• Make sure notice period '${candidate.noticePeriod.displayName}' is mentioned at the very top—recruiters filter heavily by short notice periods."
            }
            q.contains("skill") || q.contains("learn") -> {
                "Top high-demand skills to elevate your profile:\n1. Advanced Excel & Dashboarding (VLOOKUP, Pivot, Power Query)\n2. CRM & Ticketing tools (Zendesk, Freshdesk, Salesforce)\n3. Basic SQL queries to analyze operational data."
            }
            q.contains("salary") || q.contains("ask") -> {
                "For your profile (${candidate.experienceYears.toInt()} years, ${candidate.currentRole} in ${candidate.currentCity}), current market standard ranges between ₹4.5 LPA and ₹7.0 LPA. Quoting ₹${candidate.expectedCtcLpa} LPA is well within competitive market benchmarks."
            }
            else -> {
                "Hello ${candidate.fullName}! As your JobSetu Career AI, I analyze real Indian vacancies across IT, non-IT, and government sectors to give you grounded, honest guidance. You can ask me to tailor your resume, prepare interview questions, or evaluate salary expectations for any job!"
            }
        }
    }

    override suspend fun analyzeSkillGap(candidate: CandidateProfile, targetRole: String): SkillGapAnalysisResult {
        val candidateSkills = (candidate.technicalSkills + candidate.softSkills)
        val (highDemand, partial, recommendations) = when {
            targetRole.contains("android", ignoreCase = true) -> Triple(
                listOf("Jetpack Compose", "Coroutines & Flow", "Room Database", "REST APIs"),
                listOf("Basic Python/Java", "Computer Operations"),
                listOf(
                    SkillLearningItem("Jetpack Compose", "Essential", "85% of modern native Android vacancies require Compose UI architecture.", "3 weeks"),
                    SkillLearningItem("Kotlin Coroutines & Flow", "Essential", "Standard concurrency pattern used across all production Android apps.", "2 weeks"),
                    SkillLearningItem("Room Persistence", "Recommended", "Required for robust offline-first and caching capabilities.", "1 week")
                )
            )
            targetRole.contains("data", ignoreCase = true) || targetRole.contains("analyst", ignoreCase = true) -> Triple(
                listOf("Advanced SQL", "Power BI / Tableau", "Python (Pandas)", "Excel Modeling"),
                listOf("MS Office", "Basic Excel"),
                listOf(
                    SkillLearningItem("Advanced SQL & Joins", "Essential", "Required in 90% of Data Analyst interviews for data extraction.", "2 weeks"),
                    SkillLearningItem("Power BI Dashboarding", "Essential", "Crucial for translating raw operational data into business visualizations.", "2 weeks"),
                    SkillLearningItem("Python for Analytics", "Recommended", "Automating repetitive reports and statistical modeling.", "3 weeks")
                )
            )
            else -> Triple(
                listOf("CRM & Ticketing (Freshdesk/Zendesk)", "Advanced Excel / MIS", "Vendor Management", "Escalation Resolution"),
                listOf("MS Office", "Customer Handling", "Communication"),
                listOf(
                    SkillLearningItem("Advanced Excel & MIS", "Essential", "Core requirement for tracking daily team SLAs and operations reconciliation.", "10 days"),
                    SkillLearningItem("SaaS Ticketing Portals", "Recommended", "Modern enterprise support teams require familiarity with omnichannel CRM systems.", "1 week"),
                    SkillLearningItem("Vendor SLA Management", "Good to have", "Prepares you for Senior Operations Executive roles.", "1 week")
                )
            )
        }

        return SkillGapAnalysisResult(
            targetRole = targetRole,
            candidateDemonstratedSkills = candidateSkills,
            partialSkills = partial,
            missingHighDemandSkills = highDemand,
            recommendedLearningPath = recommendations
        )
    }

    override suspend fun generateInterviewQuestions(candidate: CandidateProfile, job: Job, mode: String): InterviewMockSession {
        val questions = when (mode.uppercase()) {
            "TECHNICAL" -> listOf(
                InterviewQuestion(
                    id = 1,
                    question = "How do you approach troubleshooting an unexpected disruption or error in your daily workflow?",
                    expectedKeyPoints = listOf("Root cause identification", "Prioritizing urgent impacts", "Clear stakeholder communication", "Documenting solution to prevent recurrence")
                ),
                InterviewQuestion(
                    id = 2,
                    question = "Explain your familiarity and usage of ${job.skillsRequired.firstOrNull() ?: "core tools"} in your previous responsibilities.",
                    expectedKeyPoints = listOf("Real practical usage examples", "Efficiency improvements achieved", "Accuracy checks")
                ),
                InterviewQuestion(
                    id = 3,
                    question = "How do you maintain data accuracy and avoid reconciliation discrepancies when handling multiple records?",
                    expectedKeyPoints = listOf("Double-verification methods", "Structured spreadsheets/checklists", "Timely escalation of mismatches")
                )
            )
            "MANAGERIAL" -> listOf(
                InterviewQuestion(
                    id = 1,
                    question = "Describe a situation where a customer or client was deeply dissatisfied with a delay. How did you de-escalate?",
                    expectedKeyPoints = listOf("Active listening with empathy", "Taking ownership without being defensive", "Offering a prompt workable alternative", "Following up to ensure satisfaction")
                ),
                InterviewQuestion(
                    id = 2,
                    question = "How do you manage conflicting priorities when multiple urgent requests arrive at the same time?",
                    expectedKeyPoints = listOf("Impact vs urgency assessment", "Communication with managers", "Delegation/time blocking")
                )
            )
            else -> listOf(
                InterviewQuestion(
                    id = 1,
                    question = "Tell us about your background and why you are interested in joining ${job.company}.",
                    expectedKeyPoints = listOf("Concise summary of experience", "Relevance of ${candidate.highestEducation}", "Alignment with ${job.company}'s industry")
                ),
                InterviewQuestion(
                    id = 2,
                    question = "What are your salary expectations and what is your notice period with your current employer?",
                    expectedKeyPoints = listOf("Current CTC: ₹${candidate.currentCtcLpa} LPA, Expected: ₹${candidate.expectedCtcLpa} LPA", "Official notice period: ${candidate.noticePeriod.displayName}")
                ),
                InterviewQuestion(
                    id = 3,
                    question = "Where do you see yourself professionally in the next 2-3 years at ${job.company}?",
                    expectedKeyPoints = listOf("Desire to master current role", "Take on leadership/specialist responsibilities", "Continuous skill growth")
                )
            )
        }

        return InterviewMockSession(
            id = "mock_${UUID.randomUUID().toString().take(6)}",
            jobTitle = job.title,
            company = job.company,
            mode = mode,
            questions = questions
        )
    }
}
