package xyz.secozzi.aniyomilocalmanager.data.storage

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.findFolder
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.id
import com.anggrayudi.storage.file.openInputStream
import xyz.secozzi.aniyomilocalmanager.domain.storage.AnimeDirInfo
import xyz.secozzi.aniyomilocalmanager.domain.storage.COMIC_INFO_FILE
import xyz.secozzi.aniyomilocalmanager.domain.storage.DETAILS_JSON
import xyz.secozzi.aniyomilocalmanager.domain.storage.EPISODES_JSON
import xyz.secozzi.aniyomilocalmanager.domain.storage.EPISODE_FILE_TYPES
import xyz.secozzi.aniyomilocalmanager.domain.storage.MangaDirInfo
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import java.io.InputStream
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

    override fun getOutputStream(file: DocumentFile, mode: String): OutputStream? {
        return context.contentResolver.openOutputStream(file.uri, mode)
    }

    override fun getInputStream(file: DocumentFile): InputStream? {
        return file.openInputStream(context)
    }

    override fun getFileDescriptor(file: DocumentFile, mode: String): ParcelFileDescriptor? {
        return context.contentResolver.openFileDescriptor(file.uri, mode)
    }

    override fun getAnimeEntryInformation(dir: DocumentFile, showInfo: Boolean): AnimeDirInfo {
        var hasSeason = false
        var hasCover = false
        var hasDetails = false
        var hasEpisodes = false
        var totalChildren = 0

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dir.uri, dir.id)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
        )

        context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIdx)
                if (name.startsWith(".")) continue
                totalChildren++

                val mime = cursor.getString(mimeIdx)
                if (mime == DocumentsContract.Document.MIME_TYPE_DIR) {
                    hasSeason = true
                } else {
                    if (showInfo) {
                        if (name.startsWith("cover", true)) hasCover = true
                        if (name == DETAILS_JSON) hasDetails = true
                        if (name == EPISODES_JSON) hasEpisodes = true
                    } else {
                        val extension = name.substringAfterLast(".")
                        if (extension in EPISODE_FILE_TYPES || hasSeason) {
                            return AnimeDirInfo(
                                isSeason = hasSeason,
                                hasCover = false,
                                hasDetails = false,
                                hasEpisodes = false,
                                size = null,
                            )
                        }
                    }
                }
            }
        }

        return AnimeDirInfo(
            isSeason = hasSeason,
            hasCover = hasCover,
            hasDetails = hasDetails,
            hasEpisodes = hasEpisodes,
            size = totalChildren,
        )
    }

    override fun getMangaEntryInformation(dir: DocumentFile): MangaDirInfo {
        var hasCover = false
        var hasComicInfo = false
        var totalChildren = 0

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dir.uri, dir.id)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
        )

        context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIdx)
                if (name.startsWith(".")) continue
                totalChildren++

                val mime = cursor.getString(mimeIdx)
                if (mime != DocumentsContract.Document.MIME_TYPE_DIR) {
                    if (name.startsWith("cover", true)) hasCover = true
                    if (name == COMIC_INFO_FILE) hasComicInfo = true
                }
            }
        }

        return MangaDirInfo(
            hasCover = hasCover,
            hasComicInfo = hasComicInfo,
            size = totalChildren,
        )
    }
}
