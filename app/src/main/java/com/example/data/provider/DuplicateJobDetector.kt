package com.example.data.provider

import com.example.data.model.Job
import com.example.data.model.JobDuplicateSource
import kotlin.math.max

object DuplicateJobDetector {

    /**
     * Groups jobs that represent the same position across multiple aggregators / sources.
     * Selects the official career page as the primary item where available, and attaches
     * other appearances to `duplicateSources`.
     */
    fun consolidateDuplicates(jobs: List<Job>): List<Job> {
        val groupedJobs = mutableListOf<Job>()

        for (candidateJob in jobs) {
            val existingIndex = groupedJobs.indexOfFirst { areDuplicates(it, candidateJob) }
            if (existingIndex == -1) {
                groupedJobs.add(candidateJob)
            } else {
                val existing = groupedJobs[existingIndex]
                val consolidatedSources = (existing.duplicateSources + candidateJob.duplicateSources + 
                    listOf(
                        JobDuplicateSource(
                            sourceName = candidateJob.sourceName,
                            sourceUrl = candidateJob.sourceUrl,
                            classification = candidateJob.sourceClassification,
                            isOfficial = candidateJob.isOfficialEmployerPage
                        )
                    )
                ).distinctBy { it.sourceName }

                // If candidateJob is official and existing is not, prioritize candidateJob
                val primaryJob = if (candidateJob.isOfficialEmployerPage && !existing.isOfficialEmployerPage) {
                    candidateJob.copy(
                        duplicateSources = consolidatedSources
                    )
                } else {
                    existing.copy(
                        duplicateSources = consolidatedSources
                    )
                }
                groupedJobs[existingIndex] = primaryJob
            }
        }
        return groupedJobs
    }

    fun areDuplicates(jobA: Job, jobB: Job): Boolean {
        if (jobA.id == jobB.id) return true

        // Clean company names (remove Ltd, Pvt Ltd, Inc, etc.)
        val compA = cleanCompanyName(jobA.company)
        val compB = cleanCompanyName(jobB.company)
        if (compA != compB) return false

        // Check title similarity
        val titleSim = calculateStringSimilarity(jobA.title.lowercase(), jobB.title.lowercase())
        if (titleSim < 0.75) return false

        // Check location
        val locA = jobA.locationCity.trim().lowercase()
        val locB = jobB.locationCity.trim().lowercase()
        val locMatch = locA.isEmpty() || locB.isEmpty() || locA == locB || 
                       jobA.workType == com.example.data.model.WorkType.REMOTE || 
                       jobB.workType == com.example.data.model.WorkType.REMOTE

        return locMatch
    }

    private fun cleanCompanyName(name: String): String {
        return name.lowercase()
            .replace("private limited", "")
            .replace("pvt ltd", "")
            .replace("ltd", "")
            .replace("inc", "")
            .replace("technologies", "tech")
            .replace("solutions", "")
            .replace(".", "")
            .trim()
    }

    private fun calculateStringSimilarity(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        val words1 = s1.split(" ", "/", "-").filter { it.isNotBlank() }.toSet()
        val words2 = s2.split(" ", "/", "-").filter { it.isNotBlank() }.toSet()
        val intersection = words1.intersect(words2).size
        val union = words1.union(words2).size
        if (union == 0) return 0.0
        return intersection.toDouble() / union.toDouble()
    }
}
