package io.nightfish.potatoepub

import io.nightfish.potatoepub.otf.Nav
import io.nightfish.potatoepub.otf.OpfPackage
import io.nightfish.potatoepub.otf.TocNcx
import io.nightfish.potatoepub.otf.metaInf.Container
import io.nightfish.potatoepub.xml.asFormatedXml
import org.dom4j.Document
import java.io.File
import java.io.FileInputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Epub entity
 * The implementation standard is EPUB 3.3
 * @see <a href"https://www.w3.org/TR/epub-33/">EPUB 3.3</a>
 * @param container Container
 * @param opfPackage OpfPackage
 * @param nav Nav
 * @param tocNcx ToxNcx(it's used to opf-201)
 * @param res Additional resource file map.Key is the relative path, value is the corresponding File, the user should ensure the validity of the File when calling method 'save'.
 * @param documents Additional resource file map.Key is the relative path, value is the corresponding Document
 */
@Suppress("MemberVisibilityCanBePrivate")
class Epub(
    val container: Container,
    val opfPackage: OpfPackage,
    val nav: Nav,
    val tocNcx: TocNcx,
    val documents: Map<String, Document> = mapOf(),
    private val res: Map<String, File>
) {
    companion object {
        const val MIME = "application/epub+zip"
    }

    /**
     * Save the epub object to epub file.
     * It will create a new file if the target file is not exist.
     *
     * @param target target file
     */
    fun save(target: File) {
        target.parentFile.mkdirs()
        if (!target.exists()) {
            target.createNewFile()
        }
        ZipOutputStream(target.outputStream()).use { out ->
            val mineTypeEntry = ZipEntry("mimetype")
            mineTypeEntry.method = ZipEntry.STORED
            mineTypeEntry.size = MIME.toByteArray().size.toLong()
            val crc = CRC32()
            crc.update(MIME.toByteArray())
            mineTypeEntry.crc = crc.value
            out.putNextEntry(mineTypeEntry)
            out.write(MIME.toByteArray())
            container.writeToZip(out)
            opfPackage.writeToZip(out)
            nav.writeToZip(out)
            tocNcx.writeToZip(out)
            res.forEach { entry ->
                out.putNextEntry(ZipEntry("EPUB/" + entry.key))
                FileInputStream(entry.value).use {
                    out.write(it.readBytes())
                }
            }
            documents.forEach { entry ->
                out.putNextEntry(ZipEntry("EPUB/" + entry.key))
                out.write(entry.value.asFormatedXml().toByteArray(Charsets.UTF_8))
            }
        }
    }
}