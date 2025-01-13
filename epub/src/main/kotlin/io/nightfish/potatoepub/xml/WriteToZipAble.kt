package io.nightfish.potatoepub.xml

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

interface WriteToZipAble {
    val zipEntry: ZipEntry
    fun toByteArray(): ByteArray
    fun writeToZip(zipOutStream: ZipOutputStream) {
        zipOutStream.putNextEntry(zipEntry)
        zipOutStream.write(toByteArray())
        zipOutStream.closeEntry()
    }
}