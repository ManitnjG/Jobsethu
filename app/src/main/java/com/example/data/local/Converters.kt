package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.JobDuplicateSource
import com.example.data.model.ResumeModificationDiff
import com.example.data.model.SourceClassification
import com.example.data.model.WorkType
import com.example.data.model.NoticePeriod
import com.example.data.model.ApplicationStatus
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) { }
        return list
    }

    @TypeConverter
    fun fromDuplicateSources(value: List<JobDuplicateSource>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach {
            val obj = JSONObject()
            obj.put("sourceName", it.sourceName)
            obj.put("sourceUrl", it.sourceUrl)
            obj.put("classification", it.classification.name)
            obj.put("isOfficial", it.isOfficial)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toDuplicateSources(value: String?): List<JobDuplicateSource> {
        if (value.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<JobDuplicateSource>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    JobDuplicateSource(
                        sourceName = obj.optString("sourceName", "Source"),
                        sourceUrl = obj.optString("sourceUrl", ""),
                        classification = runCatching {
                            SourceClassification.valueOf(obj.optString("classification", SourceClassification.EXTERNAL_APPLICATION.name))
                        }.getOrDefault(SourceClassification.EXTERNAL_APPLICATION),
                        isOfficial = obj.optBoolean("isOfficial", false)
                    )
                )
            }
        } catch (_: Exception) { }
        return list
    }

    @TypeConverter
    fun fromResumeDiffs(value: List<ResumeModificationDiff>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach {
            val obj = JSONObject()
            obj.put("section", it.section)
            obj.put("originalText", it.originalText)
            obj.put("proposedText", it.proposedText)
            obj.put("reasoning", it.reasoning)
            obj.put("isAccepted", it.isAccepted)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toResumeDiffs(value: String?): List<ResumeModificationDiff> {
        if (value.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<ResumeModificationDiff>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ResumeModificationDiff(
                        section = obj.optString("section", ""),
                        originalText = obj.optString("originalText", ""),
                        proposedText = obj.optString("proposedText", ""),
                        reasoning = obj.optString("reasoning", ""),
                        isAccepted = obj.optBoolean("isAccepted", true)
                    )
                )
            }
        } catch (_: Exception) { }
        return list
    }

    @TypeConverter
    fun fromWorkType(type: WorkType?): String = type?.name ?: WorkType.ON_SITE.name

    @TypeConverter
    fun toWorkType(value: String?): WorkType = runCatching {
        WorkType.valueOf(value ?: WorkType.ON_SITE.name)
    }.getOrDefault(WorkType.ON_SITE)

    @TypeConverter
    fun fromNoticePeriod(period: NoticePeriod?): String = period?.name ?: NoticePeriod.DAYS_30.name

    @TypeConverter
    fun toNoticePeriod(value: String?): NoticePeriod = runCatching {
        NoticePeriod.valueOf(value ?: NoticePeriod.DAYS_30.name)
    }.getOrDefault(NoticePeriod.DAYS_30)

    @TypeConverter
    fun fromSourceClassification(c: SourceClassification?): String = c?.name ?: SourceClassification.EXTERNAL_APPLICATION.name

    @TypeConverter
    fun toSourceClassification(value: String?): SourceClassification = runCatching {
        SourceClassification.valueOf(value ?: SourceClassification.EXTERNAL_APPLICATION.name)
    }.getOrDefault(SourceClassification.EXTERNAL_APPLICATION)

    @TypeConverter
    fun fromApplicationStatus(status: ApplicationStatus?): String = status?.name ?: ApplicationStatus.SAVED.name

    @TypeConverter
    fun toApplicationStatus(value: String?): ApplicationStatus = runCatching {
        ApplicationStatus.valueOf(value ?: ApplicationStatus.SAVED.name)
    }.getOrDefault(ApplicationStatus.SAVED)
}
