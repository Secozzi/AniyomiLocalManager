package xyz.secozzi.aniyomilocalmanager.utils

import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.fullName

/**
 * Sorts files/directories alphabetically while giving directories priority
 * credit goes to mpv-android
 */
class FilesComparator : Comparator<DocumentFile> {
    override fun compare(o1: DocumentFile, o2: DocumentFile): Int {
        val iso1ADirectory = o1.isDirectory
        val iso2ADirectory = o2.isDirectory
        if (iso1ADirectory != iso2ADirectory) return if (iso2ADirectory) 1 else -1
        return o1.fullName.compareTo(o2.fullName, ignoreCase = true)
    }
}
