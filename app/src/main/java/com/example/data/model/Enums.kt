package com.example.data.model

enum class WorkType {
    ON_SITE,
    HYBRID,
    REMOTE
}

enum class ExperienceBracket(val label: String, val minYears: Int, val maxYears: Int) {
    FRESHER("Fresher (0 yrs)", 0, 0),
    EXP_0_1("0–1 Year", 0, 1),
    EXP_1_3("1–3 Years", 1, 3),
    EXP_3_5("3–5 Years", 3, 5),
    EXP_5_10("5–10 Years", 5, 10),
    EXP_10_PLUS("10+ Years", 10, 40)
}

enum class NoticePeriod(val displayName: String, val days: Int) {
    IMMEDIATE("Immediate", 0),
    DAYS_15("15 Days", 15),
    DAYS_30("30 Days", 30),
    DAYS_45("45 Days", 45),
    DAYS_60("60 Days", 60),
    DAYS_90("90 Days", 90),
    SERVING_NOTICE("Currently Serving Notice", 5)
}

enum class SourceClassification {
    AUTHORIZED_API,
    PUBLIC_PERMITTED_FEED,
    EMPLOYER_CAREER_INTEGRATION,
    EXTERNAL_APPLICATION,
    NOT_CURRENTLY_SUPPORTED
}

enum class ApplicationStatus(val displayName: String) {
    SAVED("Saved"),
    PREPARING("Preparing"),
    APPLIED("Applied"),
    ASSESSMENT("Assessment"),
    INTERVIEW("Interview"),
    OFFER("Offer"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn");

    val label: String get() = displayName
}

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    HINDI("hi", "Hindi", "हिन्दी")
}
