package xyz.secozzi.aniyomilocalmanager.domain.storage

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream
import java.io.OutputStream

interface StorageManager {
    fun getFromPath(path: String): DocumentFile?

    fun getFile(path: String): DocumentFile?

    fun getFile(uri: Uri): DocumentFile?

    fun getPath(file: DocumentFile): String

    fun getChild(baseDirectory: DocumentFile, relative: List<String>): DocumentFile?

    fun getOutputStream(file: DocumentFile, mode: String): OutputStream?

    fun getInputStream(file: DocumentFile): InputStream?

    fun getFileDescriptor(file: DocumentFile, mode: String): ParcelFileDescriptor?
}
