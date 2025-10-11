package xyz.secozzi.aniyomilocalmanager.domain.storage

import android.net.Uri
import androidx.documentfile.provider.DocumentFile

interface StorageManager {
    fun getFile(path: String): DocumentFile?

    fun getFile(uri: Uri): DocumentFile?

    fun getChild(baseDirectory: DocumentFile, relative: List<String>): DocumentFile?
}
