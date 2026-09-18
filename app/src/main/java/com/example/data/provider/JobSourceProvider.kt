package com.example.data.provider

import com.example.data.model.Job
import com.example.data.model.SourceClassification

interface JobSourceProvider {
    val providerId: String
    val providerDisplayName: String
    val classification: SourceClassification
    val isDirectApiAuthorized: Boolean

    suspend fun fetchJobs(query: String? = null, location: String? = null): List<Job>
}
