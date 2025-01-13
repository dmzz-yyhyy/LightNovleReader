package io.nightfish.potatoepub.otf

import io.nightfish.potatoepub.xml.XmlBuilder

data class EpubManifest(
    val id: String? = null,
    val items: List<Item>
) {
    fun element(builder: XmlBuilder.ElementBuilder) {
        builder.apply {
            "manifest"("id" to id) {
                items.forEach {
                    it.element(this)
                }
            }
        }
    }

    data class Item(
        val fallback: String? = null,
        val href: String,
        val id: String,
        val mediaOverride: String? = null,
        val mediaType: String,
        val properties: String? = null,
    ) {
        fun element(builder: XmlBuilder.ElementBuilder) {
            builder.apply {
                "item"(
                    "fallback" to fallback,
                    "href" to href,
                    "id" to id,
                    "media-override" to mediaOverride,
                    "media-type" to mediaType,
                    "properties" to properties
                )
            }
        }
    }
}