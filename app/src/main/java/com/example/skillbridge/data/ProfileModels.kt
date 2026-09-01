package com.example.skillbridge.data

data class EducationEntry(val degree: String, val institution: String, val year: String)
data class ExperienceEntry(val jobTitle: String, val companyAndDuration: String)

private const val ITEM_SEP = "|||"
private const val FIELD_SEP = "::"

fun List<EducationEntry>.encodeEducation(): String =
    joinToString(ITEM_SEP) { "${it.degree}$FIELD_SEP${it.institution}$FIELD_SEP${it.year}" }

fun String?.decodeEducation(): List<EducationEntry> {
    if (this.isNullOrBlank()) return emptyList()
    return split(ITEM_SEP).mapNotNull { entry ->
        val parts = entry.split(FIELD_SEP)
        if (parts.size == 3) EducationEntry(parts[0], parts[1], parts[2]) else null
    }
}

fun List<ExperienceEntry>.encodeExperience(): String =
    joinToString(ITEM_SEP) { "${it.jobTitle}$FIELD_SEP${it.companyAndDuration}" }

fun String?.decodeExperience(): List<ExperienceEntry> {
    if (this.isNullOrBlank()) return emptyList()
    return split(ITEM_SEP).mapNotNull { entry ->
        val parts = entry.split(FIELD_SEP)
        if (parts.size == 2) ExperienceEntry(parts[0], parts[1]) else null
    }
}

fun List<String>.encodeSkills(): String = joinToString(ITEM_SEP)

fun String?.decodeSkills(): List<String> =
    if (this.isNullOrBlank()) emptyList() else split(ITEM_SEP)