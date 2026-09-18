package com.example.ui.util

import com.example.data.model.AppLanguage

object JobSetuStrings {
    fun get(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.TAMIL -> tamilStrings[key] ?: englishStrings[key] ?: key
            AppLanguage.HINDI -> hindiStrings[key] ?: englishStrings[key] ?: key
            AppLanguage.ENGLISH -> englishStrings[key] ?: key
        }
    }

    private val englishStrings = mapOf(
        "app_title" to "JobSetu AI",
        "home" to "Home",
        "search" to "Search",
        "ai" to "AI Coach",
        "applications" to "Applications",
        "profile" to "Profile",
        "search_placeholder" to "Ask AI to find your next job...",
        "best_matches" to "🔥 Best Matches",
        "jobs_posted_today" to "🆕 Jobs Posted Today",
        "recommended_for_you" to "✨ Recommended For You",
        "work_from_home" to "🏠 Work From Home",
        "near_preferred" to "📍 Jobs in Preferred Cities",
        "govt_jobs" to "🏛 Government Jobs",
        "higher_salary" to "💰 Higher Salary Opportunities",
        "it_jobs" to "💻 IT & Software",
        "non_it_jobs" to "📊 Non-IT & Operations",
        "fresher_jobs" to "🎓 Fresher Opportunities",
        "companies_hiring" to "🏢 Companies Hiring",
        "saved_jobs" to "⭐ Saved Jobs",
        "match_rate" to "% Match",
        "apply_now" to "Apply",
        "save" to "Save",
        "saved" to "Saved",
        "scam_alert" to "⚠️ Needs Review",
        "official_source" to "Verified Official",
        "available_sources" to "Available from %d sources"
    )

    private val tamilStrings = mapOf(
        "app_title" to "ஜாப்சேது AI",
        "home" to "முகப்பு",
        "search" to "தேடல்",
        "ai" to "AI ஆலோசகர்",
        "applications" to "விண்ணப்பங்கள்",
        "profile" to "சுயவிவரம்",
        "search_placeholder" to "உங்கள் அடுத்த வேலையைக் கண்டறிய AI இடம் கேளுங்கள்...",
        "best_matches" to "🔥 சிறந்த பொருத்தங்கள்",
        "jobs_posted_today" to "🆕 இன்று வெளியிடப்பட்ட வேலைகள்",
        "recommended_for_you" to "✨ உங்களுக்காக பரிந்துரைக்கப்பட்டவை",
        "work_from_home" to "🏠 வீட்டில் இருந்தே வேலை",
        "near_preferred" to "📍 நீங்கள் விரும்பும் நகர வேலைகள்",
        "govt_jobs" to "🏛 அரசு வேலைகள்",
        "higher_salary" to "💰 அதிக சம்பள வாய்ப்புகள்",
        "it_jobs" to "💻 தகவல் தொழில்நுட்பம் (IT)",
        "non_it_jobs" to "📊 பொது & செயல்பாட்டு வேலைகள்",
        "fresher_jobs" to "🎓 புதியவர்களுக்கான வாய்ப்புகள்",
        "companies_hiring" to "🏢 பணியமர்த்தும் முன்னணி நிறுவனங்கள்",
        "saved_jobs" to "⭐ சேமிக்கப்பட்ட வேலைகள்",
        "match_rate" to "% பொருத்தம்",
        "apply_now" to "விண்ணப்பி",
        "save" to "சேமி",
        "saved" to "சேமிக்கப்பட்டது",
        "scam_alert" to "⚠️ சரிபார்ப்பு தேவை",
        "official_source" to "உறுதிப்படுத்தப்பட்ட அதிகாரப்பூர்வ தளம்",
        "available_sources" to "%d தளங்களில் கிடைக்கிறது"
    )

    private val hindiStrings = mapOf(
        "app_title" to "जॉबसेतु AI",
        "home" to "होम",
        "search" to "सर्च",
        "ai" to "AI कोच",
        "applications" to "आवेदन",
        "profile" to "प्रोफाइल",
        "search_placeholder" to "अपनी अगली नौकरी ढूंढने के लिए AI से पूछें...",
        "best_matches" to "🔥 सर्वश्रेष्ठ मैच",
        "jobs_posted_today" to "🆕 आज पोस्ट की गई नौकरियां",
        "recommended_for_you" to "✨ आपके लिए अनुशंसित",
        "work_from_home" to "🏠 वर्क फ्रॉम होम",
        "near_preferred" to "📍 पसंदीदा शहरों में नौकरियां",
        "govt_jobs" to "🏛 सरकारी नौकरियां",
        "higher_salary" to "💰 उच्च वेतन के अवसर",
        "it_jobs" to "💻 आईटी एवं सॉफ्टवेयर",
        "non_it_jobs" to "📊 नॉन-आईटी एवं ऑपरेशंस",
        "fresher_jobs" to "🎓 फ्रेशर्स के लिए अवसर",
        "companies_hiring" to "🏢 हायरिंग करने वाली कंपनियां",
        "saved_jobs" to "⭐ सहेजी गई नौकरियां",
        "match_rate" to "% मैच",
        "apply_now" to "आवेदन करें",
        "save" to "सेव करें",
        "saved" to "सहेजा गया",
        "scam_alert" to "⚠️ समीक्षा आवश्यक",
        "official_source" to "सत्यापित आधिकारिक",
        "available_sources" to "%d स्रोतों पर उपलब्ध"
    )
}
