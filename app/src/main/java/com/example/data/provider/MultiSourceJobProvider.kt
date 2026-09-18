package com.example.data.provider

import com.example.data.model.Job
import com.example.data.model.SourceClassification
import com.example.data.model.WorkType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Instant
import java.time.OffsetDateTime

/**
 * Keyless multi-source real-job provider.
 * Sources are fetched independently: one outage never replaces real jobs with demo data.
 * India is the default market; worldwide remote roles eligible for India are also retained.
 */
class MultiSourceJobProvider(
    private val remotive: JobSourceProvider = AggregatorJobProvider()
) : JobSourceProvider {
    override val providerId = "jobsethu_multi_source"
    override val providerDisplayName = "Jobsethu Real Jobs"
    override val classification = SourceClassification.PUBLIC_PERMITTED_FEED
    override val isDirectApiAuthorized = true

    override suspend fun fetchJobs(query: String?, location: String?): List<Job> = coroutineScope {
        listOf<suspend () -> List<Job>>(
            { remotive.fetchJobs(query, location) },
            { fetchJobicy(query, location) },
            { fetchHimalayas(query, location) }
        ).map { source ->
            async { runCatching { source() }.getOrDefault(emptyList()) }
        }.awaitAll().flatten()
            .filter { it.sourceUrl.startsWith("https://") }
            .sortedWith(compareByDescending<Job> { indiaPriority(it, location) }.thenByDescending { it.postedTimestamp })
    }

    private suspend fun fetchJobicy(query: String?, location: String?): List<Job> = withContext(Dispatchers.IO) {
        val endpoint = buildString {
            append("https://jobicy.com/api/v2/remote-jobs?count=50")
            if (!query.isNullOrBlank()) append("&tag=").append(enc(query))
        }
        val root = getJson(endpoint)
        val array = root.optJSONArray("jobs") ?: JSONArray()
        buildList {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val geo = item.optString("jobGeo", "Remote")
                if (!locationEligible(geo, location)) continue
                val title = item.optString("jobTitle")
                if (!queryMatches(title, item.optString("jobDescription"), query)) continue
                val timestamp = parseTime(item.optString("pubDate"))
                add(Job(
                    id = "jobicy_" + item.optString("id", item.optString("url").hashCode().toString()),
                    title = title.ifBlank { "Job" },
                    company = item.optString("companyName", "Employer"),
                    companyLogoUrl = item.optString("companyLogo"),
                    locationCity = geo,
                    locationState = indiaState(geo),
                    workType = WorkType.REMOTE,
                    description = stripHtml(item.optString("jobDescription")),
                    sourceName = "Jobicy",
                    sourceUrl = item.optString("url"),
                    sourceClassification = SourceClassification.PUBLIC_PERMITTED_FEED,
                    postedTimestamp = timestamp,
                    postedTimeAgo = relativeTime(timestamp),
                    isFresherEligible = fresherText(title + " " + item.optString("jobDescription")),
                    industry = item.optString("jobIndustry", "Remote")
                ))
            }
        }
    }

    private suspend fun fetchHimalayas(query: String?, location: String?): List<Job> = withContext(Dispatchers.IO) {
        val root = getJson("https://himalayas.app/jobs/api?limit=50")
        val array = root.optJSONArray("jobs") ?: JSONArray()
        buildList {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val title = item.optString("title")
                val description = item.optString("description")
                if (!queryMatches(title, description, query)) continue
                val loc = item.optString("location", item.optString("locationRestrictions", "Remote"))
                if (!locationEligible(loc, location)) continue
                val timestamp = parseTime(item.optString("pubDate", item.optString("createdAt")))
                val url = item.optString("applicationLink", item.optString("url"))
                add(Job(
                    id = "himalayas_" + item.optString("guid", url.hashCode().toString()),
                    title = title.ifBlank { "Job" },
                    company = item.optString("companyName", item.optString("company", "Employer")),
                    companyLogoUrl = item.optString("companyLogo"),
                    locationCity = loc.ifBlank { "Remote" },
                    locationState = indiaState(loc),
                    workType = WorkType.REMOTE,
                    description = stripHtml(description),
                    sourceName = "Himalayas",
                    sourceUrl = url,
                    sourceClassification = SourceClassification.PUBLIC_PERMITTED_FEED,
                    postedTimestamp = timestamp,
                    postedTimeAgo = relativeTime(timestamp),
                    isFresherEligible = fresherText(title + " " + description),
                    industry = "Remote"
                ))
            }
        }
    }

    private fun getJson(endpoint: String): JSONObject {
        val c = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"; connectTimeout = 12_000; readTimeout = 18_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Jobsethu-Android/1.1")
        }
        return try {
            if (c.responseCode !in 200..299) JSONObject()
            else JSONObject(c.inputStream.bufferedReader().use { it.readText() })
        } finally { c.disconnect() }
    }

    private fun locationEligible(raw: String, requested: String?): Boolean {
        val s = raw.lowercase()
        val worldwide = listOf("worldwide","anywhere","global","remote").any(s::contains)
        val india = listOf("india","chennai","bengaluru","bangalore","hyderabad","pune","mumbai","delhi","gurugram","gurgaon","noida","kolkata","kochi","coimbatore").any(s::contains)
        if (!requested.isNullOrBlank()) return s.contains(requested.lowercase()) || worldwide
        return india || worldwide || raw.isBlank()
    }

    private fun indiaPriority(job: Job, requested: String?): Int {
        val loc = (job.locationCity + " " + job.locationState).lowercase()
        if (!requested.isNullOrBlank() && loc.contains(requested.lowercase())) return 3
        if (loc.contains("india")) return 2
        return if (job.workType == WorkType.REMOTE) 1 else 0
    }

    private fun indiaState(loc: String) = if (loc.contains("india", true)) "India" else "Remote / India eligible"
    private fun queryMatches(title: String, desc: String, q: String?) =
        q.isNullOrBlank() || (title + " " + desc).contains(q, true)
    private fun fresherText(s: String) = listOf("fresher","entry level","graduate","0-1 year","0–1 year").any { s.contains(it, true) }
    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")
    private fun stripHtml(s: String) = s.replace(Regex("<[^>]*>"), " ").replace(Regex("\\s+"), " ").trim()
    private fun parseTime(s: String): Long = runCatching { Instant.parse(s).toEpochMilli() }
        .recoverCatching { OffsetDateTime.parse(s).toInstant().toEpochMilli() }.getOrDefault(System.currentTimeMillis())
    private fun relativeTime(t: Long): String {
        val h = ((System.currentTimeMillis() - t).coerceAtLeast(0) / 3_600_000)
        return when { h < 1 -> "Just now"; h < 24 -> "${h}h ago"; else -> "${h/24}d ago" }
    }
}
