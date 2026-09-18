package com.example.data.provider

import com.example.data.model.Job
import com.example.data.model.SourceClassification
import com.example.data.model.WorkType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Instant

/**
 * Fetches REAL jobs from Remotive's free public API.
 * No API key is required. Remotive requires attribution and link-back,
 * so every Job keeps sourceName="Remotive" and its canonical listing URL.
 */
class AggregatorJobProvider : JobSourceProvider {
    override val providerId = "remotive_public_api"
    override val providerDisplayName = "Remotive Remote Jobs"
    override val classification = SourceClassification.PUBLIC_PERMITTED_FEED
    override val isDirectApiAuthorized = true

    override suspend fun fetchJobs(query: String?, location: String?): List<Job> = withContext(Dispatchers.IO) {
        val endpoint = buildString {
            append("https://remotive.com/api/remote-jobs?limit=100")
            if (!query.isNullOrBlank()) append("&search=").append(URLEncoder.encode(query, "UTF-8"))
        }
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 20_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Jobsethu-Android/1.0")
        }
        try {
            if (connection.responseCode !in 200..299) return@withContext emptyList()
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val jobs = JSONObject(body).optJSONArray("jobs") ?: return@withContext emptyList()
            buildList {
                for (i in 0 until jobs.length()) {
                    val item = jobs.optJSONObject(i) ?: continue
                    val requiredLocation = item.optString("candidate_required_location", "Remote")
                    if (!location.isNullOrBlank() &&
                        !requiredLocation.contains(location, ignoreCase = true) &&
                        !requiredLocation.contains("worldwide", ignoreCase = true) &&
                        !requiredLocation.contains("anywhere", ignoreCase = true)
                    ) continue

                    val publication = item.optString("publication_date")
                    val timestamp = runCatching { Instant.parse(publication).toEpochMilli() }.getOrDefault(System.currentTimeMillis())
                    val html = item.optString("description")
                    val plainDescription = html
                        .replace(Regex("<[^>]*>"), " ")
                        .replace(Regex("\\s+"), " ")
                        .trim()

                    add(
                        Job(
                            id = "remotive_${item.optLong("id")}",
                            title = item.optString("title", "Job"),
                            company = item.optString("company_name", "Employer"),
                            companyLogoUrl = item.optString("company_logo"),
                            locationCity = requiredLocation,
                            locationState = "",
                            workType = WorkType.REMOTE,
                            description = plainDescription,
                            skillsRequired = jsonStrings(item.optJSONArray("tags")),
                            sourceName = "Remotive",
                            sourceUrl = item.optString("url"),
                            sourceClassification = SourceClassification.PUBLIC_PERMITTED_FEED,
                            isOfficialEmployerPage = false,
                            postedTimeAgo = relativeTime(timestamp),
                            postedTimestamp = timestamp,
                            isGovernment = false,
                            isFresherEligible = false,
                            isItRole = item.optString("category").contains("software", true) ||
                                item.optString("category").contains("devops", true) ||
                                item.optString("category").contains("data", true),
                            industry = item.optString("category", "Remote")
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        } finally {
            connection.disconnect()
        }
    }

    private fun jsonStrings(array: org.json.JSONArray?): List<String> {
        if (array == null) return emptyList()
        return (0 until array.length()).mapNotNull { array.optString(it).takeIf(String::isNotBlank) }
    }

    private fun relativeTime(timestamp: Long): String {
        val hours = ((System.currentTimeMillis() - timestamp).coerceAtLeast(0) / 3_600_000)
        return when {
            hours < 1 -> "Just now"
            hours < 24 -> "${hours}h ago"
            else -> "${hours / 24}d ago"
        }
    }
}
