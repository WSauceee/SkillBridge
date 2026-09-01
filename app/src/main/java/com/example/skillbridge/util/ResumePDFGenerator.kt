package com.example.skillbridge.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.skillbridge.data.EducationEntry
import com.example.skillbridge.data.ExperienceEntry
import com.example.skillbridge.data.User
import java.io.File
import java.io.FileOutputStream

object ResumePdfGenerator {

    fun generate(
        context: Context,
        user: User,
        education: List<EducationEntry>,
        skills: List<String>,
        experience: List<ExperienceEntry>
    ): File {
        val pageWidth = 595   // A4 at 72dpi
        val pageHeight = 842
        val margin = 40f

        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val canvas = page.canvas

        val titlePaint = Paint().apply { textSize = 22f; typeface = Typeface.DEFAULT_BOLD }
        val headerPaint = Paint().apply { textSize = 14f; typeface = Typeface.DEFAULT_BOLD }
        val bodyPaint = Paint().apply { textSize = 12f }

        var y = margin + 20f

        canvas.drawText(user.fullName, margin, y, titlePaint)
        y += 20f
        canvas.drawText(user.email, margin, y, bodyPaint)
        if (!user.extraInfo.isNullOrBlank()) {
            y += 16f
            canvas.drawText(user.extraInfo, margin, y, bodyPaint)
        }

        y += 30f
        canvas.drawText("Education", margin, y, headerPaint)
        y += 18f
        if (education.isEmpty()) {
            canvas.drawText("—", margin, y, bodyPaint)
            y += 16f
        } else {
            education.forEach {
                canvas.drawText("${it.degree} — ${it.institution} (${it.year})", margin, y, bodyPaint)
                y += 16f
            }
        }

        y += 14f
        canvas.drawText("Skills", margin, y, headerPaint)
        y += 18f
        canvas.drawText(if (skills.isEmpty()) "—" else skills.joinToString(", "), margin, y, bodyPaint)
        y += 16f

        y += 14f
        canvas.drawText("Experience", margin, y, headerPaint)
        y += 18f
        if (experience.isEmpty()) {
            canvas.drawText("—", margin, y, bodyPaint)
            y += 16f
        } else {
            experience.forEach {
                canvas.drawText(it.jobTitle, margin, y, bodyPaint)
                y += 16f
                canvas.drawText(it.companyAndDuration, margin + 10f, y, bodyPaint)
                y += 16f
            }
        }

        document.finishPage(page)

        val fileName = "resume_${user.fullName.replace(" ", "_")}.pdf"
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return file
    }

    fun getShareUri(context: Context, file: File) =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}