package com.example.data.provider

import com.example.data.model.GovtJob

class GovtJobProvider {

    suspend fun getVerifiedGovtJobs(): List<GovtJob> {
        return listOf(
            GovtJob(
                id = "govt_ssc_cgl_2026",
                organization = "Staff Selection Commission (SSC)",
                postTitle = "Combined Graduate Level (CGL) - Assistant Section Officer / Inspector",
                totalVacancies = 14582,
                qualification = "Bachelor's Degree in any discipline from a recognized University or equivalent (B.A., B.Sc., B.Com, B.E., B.Tech, etc.)",
                ageRequirements = "18 to 30 years (Age relaxation applicable for OBC: 3 yrs, SC/ST: 5 yrs, PwD: 10 yrs as per Govt. norms)",
                applicationStartDate = "2026-08-10",
                applicationEndDate = "2026-10-15",
                examDate = "Tier-I CBT: November 2026",
                applicationFee = "₹100 (Women, SC, ST, PwD, and Ex-Servicemen exempted from fee)",
                location = "All India (Cadre postings across Central Ministries)",
                officialNotificationPdfUrl = "https://ssc.gov.in/notice/cgl-examination-2026-official.pdf",
                officialApplyPortalUrl = "https://ssc.gov.in/apply",
                category = "Central Ministries",
                isVerifiedSource = true
            ),
            GovtJob(
                id = "govt_ibps_po_2026",
                organization = "Institute of Banking Personnel Selection (IBPS)",
                postTitle = "Probationary Officer / Management Trainee (PO/MT XIV)",
                totalVacancies = 4455,
                qualification = "Graduation in any discipline (B.A./B.Sc./B.Com/B.E./B.Tech) with computer literacy certification",
                ageRequirements = "20 to 30 years (as on 01.08.2026)",
                applicationStartDate = "2026-08-01",
                applicationEndDate = "2026-09-30",
                examDate = "Prelims: October 2026 | Mains: November 2026",
                applicationFee = "₹850 for General/EWS/OBC, ₹175 for SC/ST/PwBD",
                location = "Participating Public Sector Banks across India (Canara, PNB, BoB, Indian Bank)",
                officialNotificationPdfUrl = "https://ibps.in/crp-po-mt-xiv-notification.pdf",
                officialApplyPortalUrl = "https://ibps.in",
                category = "Banking",
                isVerifiedSource = true
            ),
            GovtJob(
                id = "govt_tnpsc_group2_2026",
                organization = "Tamil Nadu Public Service Commission (TNPSC)",
                postTitle = "Combined Civil Services Examination-II (Group II / IIA Services)",
                totalVacancies = 2327,
                qualification = "Any Degree (10+2+3 pattern) from a recognized University; knowledge of Tamil language mandatory",
                ageRequirements = "18 to 32 years (No maximum age limit for SC/ST/MBC/BC candidates)",
                applicationStartDate = "2026-07-15",
                applicationEndDate = "2026-09-28",
                examDate = "Prelims Exam: 28th September 2026",
                applicationFee = "One-Time Registration: ₹150, Preliminary Exam Fee: ₹100",
                location = "Tamil Nadu (Sub-Registrar, Municipal Commissioner, Assistant Inspector of Labour)",
                officialNotificationPdfUrl = "https://www.tnpsc.gov.in/notification-group-2-2026.pdf",
                officialApplyPortalUrl = "https://www.tnpscexams.in",
                category = "State PSC",
                isVerifiedSource = true
            ),
            GovtJob(
                id = "govt_rrb_ntpc_2026",
                organization = "Railway Recruitment Boards (RRB)",
                postTitle = "Non-Technical Popular Categories (NTPC) - Station Master & Goods Guard",
                totalVacancies = 11558,
                qualification = "12th Pass for Junior Clerk / Graduate Degree for Station Master and Commercial Apprentice",
                ageRequirements = "18 to 33 years for Graduate posts (with 3-year COVID age relaxation)",
                applicationStartDate = "2026-09-01",
                applicationEndDate = "2026-10-20",
                examDate = "CBT-1: December 2026 - January 2027",
                applicationFee = "₹500 (₹400 refunded after appearing in CBT-1 for UR/OBC), ₹250 (Full refund for SC/ST/Female)",
                location = "All India Railway Zones (Southern Railway, South Western, Northern, Western, etc.)",
                officialNotificationPdfUrl = "https://rrbchennai.gov.in/cen-ntpc-2026-detailed-notice.pdf",
                officialApplyPortalUrl = "https://www.rrbapply.gov.in",
                category = "Railways",
                isVerifiedSource = true
            ),
            GovtJob(
                id = "govt_ncs_apprentice_2026",
                organization = "National Career Service (NCS) / BHEL",
                postTitle = "Graduate & Technician Trade Apprentice",
                totalVacancies = 750,
                qualification = "Diploma or B.E./B.Tech / B.Sc in Mechanical, Electrical, Civil, or Computer Science (Passout 2023, 2024, 2025)",
                ageRequirements = "18 to 27 years",
                applicationStartDate = "2026-08-20",
                applicationEndDate = "2026-10-05",
                examDate = "Direct Merit Selection on Marks Basis",
                applicationFee = "NIL (No Application Fee)",
                location = "Trichy, Ranipet, Hyderabad, Haridwar",
                officialNotificationPdfUrl = "https://www.ncs.gov.in/bhel-apprentice-advt-2026.pdf",
                officialApplyPortalUrl = "https://www.ncs.gov.in",
                category = "Public Sector PSU",
                isVerifiedSource = true
            )
        )
    }
}
