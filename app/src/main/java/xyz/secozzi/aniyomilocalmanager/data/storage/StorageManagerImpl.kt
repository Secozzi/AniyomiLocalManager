package xyz.secozzi.aniyomilocalmanager.data.storage

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.findFolder
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.openOutputStream
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import java.io.OutputStream

class StorageManagerImpl(private val context: Context) : StorageManager {
    override fun getFromPath(path: String): DocumentFile? {
        return DocumentFileCompat.fromFullPath(context, path, requiresWriteAccess = true)
    }

    override fun getFile(path: String): DocumentFile? {
        return DocumentFileCompat.fromUri(context, path.toUri())
    }

    override fun getFile(uri: Uri): DocumentFile? {
        return DocumentFileCompat.fromUri(context, uri)
    }

    override fun getPath(file: DocumentFile): String {
        return file.getAbsolutePath(context)
    }

    override fun getChild(
        baseDirectory: DocumentFile,
        relative: List<String>,
    ): DocumentFile? {
        var current = baseDirectory
        relative.forEach {
            current = current.findFolder(it) ?: return null
        }
        return current
    }

    override fun getOutputStream(file: DocumentFile, append: Boolean): OutputStream? {
        return file.openOutputStream(context, append)
    }
}
