package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidate_profile")
data class CandidateProfile(
    @PrimaryKey val id: String = "primary_candidate",
    val fullName: String = "Arun Kumar",
    val email: String = "arun.kumar@example.com",
    val phone: String = "+91 98765 43210",
    val currentRole: String = "Tour Operations Executive",
    val desiredRoles: List<String> = listOf("Operations Executive", "Travel Consultant", "Customer Support Specialist"),
    val experienceYears: Double = 3.0,
    val highestEducation: String = "B.Sc Computer Science",
    val university: String = "Madras University",
    val graduationYear: Int = 2023,
    val currentCity: String = "Chennai",
    val currentState: String = "Tamil Nadu",
    val preferredCities: List<String> = listOf("Chennai", "Bengaluru", "Coimbatore"),
    val technicalSkills: List<String> = listOf("MS Office", "Excel", "Computer Operations", "CRM Software", "Basic Python"),
    val softSkills: List<String> = listOf("Customer Handling", "Vendor Coordination", "English & Tamil Communication", "Problem Solving"),
    val currentCtcLpa: Double = 4.2,
    val expectedCtcLpa: Double = 6.5,
    val noticePeriod: NoticePeriod = NoticePeriod.DAYS_30,
    val isServingNotice: Boolean = false,
    val workPreference: WorkType = WorkType.HYBRID,
    val languagesKnown: List<String> = listOf("English", "Tamil", "Hindi"),
    val summary: String = "Energetic Operations & Customer Support Professional with 3 years of experience in travel operations, hotel reservations, customer handling and workflow coordination. Graduated with B.Sc Computer Science."
)

data class WorkHistoryItem(
    val company: String,
    val role: String,
    val duration: String,
    val location: String,
    val highlights: List<String>
)

data class EducationItem(
    val degree: String,
    val institution: String,
    val year: String,
    val score: String
)
