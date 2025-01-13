package io.nightfish.potatoepub.otf

import io.nightfish.potatoepub.xml.Attribute
import io.nightfish.potatoepub.xml.TextDirection
import io.nightfish.potatoepub.xml.Version
import io.nightfish.potatoepub.xml.WriteToZipAble
import io.nightfish.potatoepub.xml.XmlBuilder.Companion.xml
import io.nightfish.potatoepub.xml.XmlLang
import io.nightfish.potatoepub.xml.asFormatedXml
import java.util.zip.ZipEntry

data class OpfPackage(
    val dir: TextDirection = TextDirection.LTR,
    val id: String = "opf-package",
    val lang: String = "en",
    val metadata: Metadata,
    val manifest: EpubManifest,
    val spine: Spine
) : WriteToZipAble {
    override val zipEntry: ZipEntry = ZipEntry("EPUB/content.opf")
    override fun toByteArray(): ByteArray =
        xml(
            "package",
            "http://www.idpf.org/2007/opf",
            Attribute("unique-identifier", "pub-id"),
            Version("3.0"),
            Attribute("prefix", "rendition: http://www.idpf.org/vocab/rendition/#"),
            Attribute("xmlns:dc", "http://purl.org/dc/elements/1.1/"),
            Attribute("xmlns:dc", "http://www.idpf.org/2007/opf"),
            dir,
            XmlLang(lang)
        ) {
            element
                .addNamespace("opt", "http://www.idpf.org/2007/opf")
                .addNamespace("dc", "http://purl.org/dc/elements/1.1/")
            metadata.element(this)
            manifest.element(this)
            spine.element(this)
        }
            .asFormatedXml().toByteArray(Charsets.UTF_8)
}