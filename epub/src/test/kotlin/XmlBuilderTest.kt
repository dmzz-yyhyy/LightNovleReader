import io.nightfish.potatoepub.xml.XmlBuilder.Companion.xml
import io.nightfish.potatoepub.xml.asFormatedXml

fun main() {
    println(
        xml("html") {
            "html"("lang" to "zh_CN") {
                "body"(
                    "href" to "https://pronhub.com",
                    "div" to "RTL"
                ) {
                    "click for 0721"
                }
                "p" {
                    "Ciallo~(∠・ω< )⌒☆"
                }
            }
        }.addDocType("html", "", "")
            .asFormatedXml()
    )
}