package io.nightfish.potatoepub.xml

import java.io.StringWriter
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter


fun Document.asFormatedXml(): String {
    val format = OutputFormat()
    format.encoding = "UTF-8"
    format.isNewlines = true
    format.indent = "  "
    format.isExpandEmptyElements = false
    val strWtr = StringWriter()
    val xmlWrt = XMLWriter(strWtr, format)
    xmlWrt.write(DocumentHelper.parseText(this.asXML()))
    xmlWrt.flush()
    xmlWrt.close()
    return strWtr.toString()
        .replaceFirst(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        )
}