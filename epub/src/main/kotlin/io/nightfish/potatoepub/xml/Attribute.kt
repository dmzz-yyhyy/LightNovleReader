package io.nightfish.potatoepub.xml

open class Attribute(val name: String, val value: Any?) {
    companion object {
        val empty = Attribute(name = "", value = null)
    }
}
