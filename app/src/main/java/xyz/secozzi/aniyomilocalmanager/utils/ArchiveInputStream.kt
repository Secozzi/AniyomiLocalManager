// From https://github.com/mihonapp/mihon/blob/main/core/archive

package xyz.secozzi.aniyomilocalmanager.utils

import me.zhanghai.android.libarchive.Archive
import me.zhanghai.android.libarchive.ArchiveEntry
import me.zhanghai.android.libarchive.ArchiveException
import xyz.secozzi.aniyomilocalmanager.domain.storage.ArchiveEntryData
import java.io.InputStream
import java.nio.ByteBuffer

internal class ArchiveInputStream(buffer: Long, size: Long) : InputStream() {
    private val lock = Any()

    @Volatile
    private var isClosed = false

    private val archive = Archive.readNew()

    init {
        try {
            Archive.setCharset(archive, Charsets.UTF_8.name().toByteArray())
            Archive.readSupportFilterAll(archive)
            Archive.readSupportFormatAll(archive)
            Archive.readOpenMemoryUnsafe(archive, buffer, size)
        } catch (e: ArchiveException) {
            close()
            throw e
        }
    }

    private val oneByteBuffer = ByteBuffer.allocateDirect(1)

    override fun read(): Int {
        read(oneByteBuffer)
        return if (oneByteBuffer.hasRemaining()) oneByteBuffer.get().toUByte().toInt() else -1
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val buffer = ByteBuffer.wrap(b, off, len)
        read(buffer)
        return if (buffer.hasRemaining()) buffer.remaining() else -1
    }

    private fun read(buffer: ByteBuffer) {
        buffer.clear()
        Archive.readData(archive, buffer)
        buffer.flip()
    }

    override fun close() {
        synchronized(lock) {
            if (isClosed) return
            isClosed = true
        }

        Archive.readFree(archive)
    }

    fun getNextFileName() = Archive.readNextHeader(archive).takeUnless { it == 0L }?.let { entry ->
        if (ArchiveEntry.filetype(entry) != ArchiveEntry.AE_IFREG) {
            return@let null
        }

        ArchiveEntry.pathnameUtf8(entry)
            ?: ArchiveEntry.pathname(entry)?.decodeToString()
            ?: return null
    }

    fun getNextArchiveEntry() = Archive.readNextHeader(archive).takeUnless { it == 0L }?.let { entry ->
        if (ArchiveEntry.filetype(entry) != ArchiveEntry.AE_IFREG) {
            return@let null
        }

        ArchiveEntryData(
            name = ArchiveEntry.pathnameUtf8(entry)
                ?: ArchiveEntry.pathname(entry)?.decodeToString()
                ?: return null,
            mTime = ArchiveEntry.mtime(entry),
            mTimeNsec = ArchiveEntry.mtimeNsec(entry),
            perm = ArchiveEntry.perm(entry),
            size = ArchiveEntry.size(entry).takeIf { ArchiveEntry.sizeIsSet(entry) },
            fileType = ArchiveEntry.filetype(entry),
        )
    }
}
