package xyz.secozzi.aniyomilocalmanager.data.storage

import android.os.ParcelFileDescriptor
import android.system.Os
import android.system.OsConstants
import me.zhanghai.android.libarchive.ArchiveException
import xyz.secozzi.aniyomilocalmanager.domain.storage.ArchiveEntryData
import xyz.secozzi.aniyomilocalmanager.domain.storage.ArchiveReader
import xyz.secozzi.aniyomilocalmanager.utils.ArchiveInputStream
import java.io.InputStream

class ArchiveReaderImpl(
    pfd: ParcelFileDescriptor,
) : ArchiveReader {
    private val size = pfd.statSize
    private val address = Os.mmap(0, size, OsConstants.PROT_READ, OsConstants.MAP_PRIVATE, pfd.fileDescriptor, 0)

    override fun <T> useEntries(block: (Sequence<ArchiveEntryData>) -> T): T {
        return ArchiveInputStream(address, size).use {
            block(generateSequence { it.getNextArchiveEntry() })
        }
    }

    override fun getInputStream(entryName: String): InputStream? {
        val archive = ArchiveInputStream(address, size)
        try {
            while (true) {
                val entry = archive.getNextFileName() ?: break
                if (entry == entryName) {
                    return archive
                }
            }
        } catch (e: ArchiveException) {
            archive.close()
            throw e
        }
        archive.close()
        return null
    }

    override fun close() {
        Os.munmap(address, size)
    }
}
