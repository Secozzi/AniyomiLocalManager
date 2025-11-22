package xyz.secozzi.aniyomilocalmanager.domain.storage

import androidx.documentfile.provider.DocumentFile
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

interface ZipWriter : Closeable {
    fun write(file: DocumentFile)

    fun write(archiveEntry: ArchiveEntryData, inputStream: InputStream)

    fun write(name: String, inputStream: InputStream)

    fun newDataOutputStream(): OutputStream
}
