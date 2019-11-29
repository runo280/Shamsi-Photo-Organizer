package io.runo280.shamsi_photo_organizer

import com.drew.imaging.ImageMetadataReader
import com.hosseini.persian.dt.PersianDT
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.BiPredicate
import java.util.regex.Pattern
import kotlin.streams.toList

object Utils {
    private const val DATE_TIME_PATTEN = "((\\d{4})[-:]?(\\d{2})[-:]?(\\d{2})\\s(\\d{2}):(\\d{2}):(\\d{2}))"
    @JvmStatic
    fun getPhotosList(dir: File): List<Photo> {
        val maxDepth = 999
        return Files.find(Paths.get(dir.absolutePath), maxDepth, BiPredicate { path: Path, _: BasicFileAttributes? ->
            Pattern.compile("(?i:(jpg)$|(jpeg)$)")
                    .matcher(path.toString())
                    .find()
        }).toList().asSequence().filterNotNull().map { it.toFile() }.map {
            info("Find image: ${it.absolutePath}")
            val photo = Photo(it)
            photo.metadata = extractMetaData(photo.file)
            photo
        }.filter {
            it.hasDate()
        }.toList()
    }

    private fun extractMetaData(file: File): String = try {
        val stringBuilder = StringBuilder()
        ImageMetadataReader.readMetadata(file).directories.filterNotNull().forEach {
            println("dir: ${it.name}\n- tags_count: ${it.tagCount}")
            it.tags.filterNotNull().filter { tag ->
                !"File Name".equals(tag.tagName, ignoreCase = true)
            }.forEach { tag ->
                println(tag.tagName)
                stringBuilder.append(tag.tagName + " " + tag.description + "\n")
            }
        }
        stringBuilder.toString()
    } catch (e: Throwable) {
        error(e)
        "null"
    }

    private fun parseDate(dateString: String): Date? {
        val trimmed = dateString.trim { it <= ' ' }
        val firstFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
        val secondFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return try {
            firstFormat.parse(trimmed)
        } catch (e: ParseException) {
            error(e)
            try {
                secondFormat.parse(trimmed)
            } catch (e1: ParseException) {
                error(e)
                null
            }
        }
    }

    @Throws(IOException::class)
    private fun renameOrMoveFile(oldFile: File, newName: String, outDir: String, year: String, rename: Boolean) {
        val pathName = "$outDir/$year"
        val path = File(pathName)
        if (!path.exists()) {
            path.mkdirs()
        }
        val newFile = File(if (rename) "$pathName/Photo_$newName" else "$pathName/${oldFile.name}")
        if (oldFile.renameTo(newFile)) {
            println() //TODO success message
        } else {
            error("Failed: ${oldFile.absolutePath}")
        }
    }


    @JvmStatic
    fun rename(photo: Photo, outputPath: String, shouldRename: Boolean) {
        val datePattern = Pattern.compile(DATE_TIME_PATTEN)
        val matcher = datePattern.matcher(photo.metadata)
        var dateString = ""
        if (matcher.find()) {
            photo.hour = matcher.group(5)
            photo.minute = matcher.group(6)
            photo.second = matcher.group(7)
            dateString = matcher.group(1)
        } else {
            println() // TODO failed
        }
        val date = parseDate(dateString)
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val generate = PersianDT
                    .Instance()
                    .generate(date, "{DATE}")
                    .Separator("-")
            val newName = String.format("%s_%s-%s-%s.jpg",
                    generate.withFullDateInDigits,
                    photo.hour, photo.minute, photo.second
            )
            try {
                renameOrMoveFile(photo.file, newName, outputPath, generate.year.toString(), shouldRename)
            } catch (e: IOException) {
                error(e)
                e.printStackTrace()
            }
        }
    }
}