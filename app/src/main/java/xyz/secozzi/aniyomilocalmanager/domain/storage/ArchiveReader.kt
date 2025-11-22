package xyz.secozzi.aniyomilocalmanager.domain.storage

import java.io.Closeable
import java.io.InputStream

interface ArchiveReader : Closeable {
    fun <T> useEntries(block: (Sequence<ArchiveEntryData>) -> T): T

    fun getInputStream(entryName: String): InputStream?
}

data class ArchiveEntryData(
    val name: String,
    val mTime: Long,
    val mTimeNsec: Long,
    val perm: Int,
    val size: Long?,
    val fileType: Int,
)
