package xyz.secozzi.aniyomilocalmanager.data.storage

import android.content.Context
import android.system.Os
import android.system.StructStat
import androidx.documentfile.provider.DocumentFile
import me.zhanghai.android.libarchive.Archive
import me.zhanghai.android.libarchive.ArchiveEntry
import me.zhanghai.android.libarchive.ArchiveException
import xyz.secozzi.aniyomilocalmanager.domain.storage.ArchiveEntryData
import xyz.secozzi.aniyomilocalmanager.domain.storage.ZipWriter
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

// Code stolen from https://github.com/mihonapp/mihon/blob/main/core/archive/src/main/kotlin/mihon/core/archive/ZipWriter.kt
// and https://github.com/zhanghai/MaterialFiles/blob/master/app/src/main/java/me/zhanghai/android/files/provider/archive/archiver/ArchiveWriter.kt
class ZipWriterImpl(
    file: DocumentFile,
    private val context: Context,
) : ZipWriter {
    private val pfd = context.contentResolver.openFileDescriptor(file.uri, "wt")!!
    private val archive = Archive.writeNew()
    private val entry = ArchiveEntry.new2(archive)
    private val buffer = ByteBuffer.allocateDirect(8192)

    init {
        try {
            Archive.setCharset(archive, Charsets.UTF_8.name().toByteArray())
            Archive.writeSetFormatZip(archive)
            Archive.writeZipSetCompressionStore(archive)
            Archive.writeOpenFd(archive, pfd.fd)
        } catch (e: ArchiveException) {
            close()
            throw e
        }
    }

    override fun write(file: DocumentFile) {
        context.contentResolver.openFileDescriptor(file.uri, "r")!!.use {
            val fd = it.fileDescriptor
            ArchiveEntry.clear(entry)
            ArchiveEntry.setPathnameUtf8(entry, file.name)
            val stat = Os.fstat(fd)
            ArchiveEntry.setStat(entry, stat.toArchiveStat())
            Archive.writeHeader(archive, entry)
            while (true) {
                buffer.clear()
                Os.read(fd, buffer)
                if (buffer.position() == 0) break
                buffer.flip()
                Archive.writeData(archive, buffer)
            }
            Archive.writeFinishEntry(archive)
        }
    }

    override fun write(archiveEntry: ArchiveEntryData, inputStream: InputStream) {
        ArchiveEntry.clear(entry)
        ArchiveEntry.setPathnameUtf8(entry, archiveEntry.name)
        ArchiveEntry.setMtime(entry, archiveEntry.mTime, archiveEntry.mTimeNsec)
        ArchiveEntry.setPerm(entry, archiveEntry.perm)
        archiveEntry.size?.let { ArchiveEntry.setSize(entry, it) }
        ArchiveEntry.setFiletype(entry, archiveEntry.fileType)
        Archive.writeHeader(archive, entry)

        inputStream.use { i ->
            i.copyTo(newDataOutputStream())
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun write(name: String, inputStream: InputStream) {
        ArchiveEntry.clear(entry)
        ArchiveEntry.setPathnameUtf8(entry, name)
        val now = Clock.System.now().toJavaInstant()
        ArchiveEntry.setMtime(entry, now.epochSecond, now.nano.toLong())
        ArchiveEntry.setFiletype(entry, ArchiveEntry.AE_IFREG)
        ArchiveEntry.setPerm(entry, 0b110100100) // (rw-r--r--)
        Archive.writeHeader(archive, entry)

        inputStream.use { i ->
            i.copyTo(newDataOutputStream())
        }
    }

    override fun close() {
        ArchiveEntry.free(entry)
        Archive.writeFree(archive)
        pfd.close()
    }

    override fun newDataOutputStream(): OutputStream {
        return DataOutputStream()
    }

    private inner class DataOutputStream : OutputStream() {
        private val oneByteBuffer = ByteBuffer.allocateDirect(1)

        override fun write(b: Int) {
            oneByteBuffer.clear()
            oneByteBuffer.put(b.toByte())
            Archive.writeData(archive, oneByteBuffer)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            val buffer = ByteBuffer.wrap(b, off, len)
            while (buffer.hasRemaining()) {
                Archive.writeData(archive, buffer)
            }
        }
    }
}

private fun StructStat.toArchiveStat() = ArchiveEntry.StructStat().apply {
    stDev = st_dev
    stMode = st_mode
    stNlink = st_nlink.toInt()
    stUid = st_uid
    stGid = st_gid
    stRdev = st_rdev
    stSize = st_size
    stBlksize = st_blksize
    stBlocks = st_blocks
    stAtim = st_atime.toTimespec()
    stMtim = st_mtime.toTimespec()
    stCtim = st_ctime.toTimespec()
    stIno = st_ino
}

private fun Long.toTimespec() = ArchiveEntry.StructTimespec().also { it.tvSec = this }
