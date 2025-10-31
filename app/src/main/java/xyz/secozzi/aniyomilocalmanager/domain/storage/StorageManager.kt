package xyz.secozzi.aniyomilocalmanager.domain.storage

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.OutputStream

interface StorageManager {
    fun getFromPath(path: String): DocumentFile?

    fun getFile(path: String): DocumentFile?

    fun getFile(uri: Uri): DocumentFile?

    fun getPath(file: DocumentFile): String

    fun getChild(baseDirectory: DocumentFile, relative: List<String>): DocumentFile?

    fun getOutputStream(file: DocumentFile, append: Boolean): OutputStream?
}
