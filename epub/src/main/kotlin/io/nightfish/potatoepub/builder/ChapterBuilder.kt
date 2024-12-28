package io.nightfish.potatoepub.builder

import org.dom4j.Document

class ChapterBuilder {
    private var title: String? = null
    private var content: Document? = null
    private val chapters: MutableList<Chapter> = mutableListOf()
    private val _contentBuilders: MutableList<SimpleContentBuilder> = mutableListOf()
    val contentBuilders: List<SimpleContentBuilder> = _contentBuilders

    fun title(title: String) {
        this.title = title
    }

    fun content(content: Document) {
        if (chapters.isNotEmpty()) throw Error("You can only use either 'content' or 'chapters' method")
        this.content = content
    }

    fun content(builder: SimpleContentBuilder.() -> Unit) {
        if (chapters.isNotEmpty()) throw Error("You can only use either 'content' or 'chapters' method")
        val content = SimpleContentBuilder().let {
            builder.invoke(it)
            _contentBuilders.add(it)
            it.build()
        }
        this.content = content
    }

    fun chapter(chapter: Chapter) {
        if (content != null) throw Error("You can only use either 'content' or 'chapters' method")
        chapters.add(chapter)
    }

    fun chapter(builder: ChapterBuilder.() -> Unit) {
        if (content != null) throw Error("You can only use either 'content' or 'chapters' method")
        val chapter = ChapterBuilder().let {
            builder.invoke(it)
            val chapter = it.build()
            this._contentBuilders.addAll(it.contentBuilders)
            chapter
        }
        chapters.add(chapter)
    }

    fun build(): Chapter {
        title ?: throw Error("Missing 'title'")
        if (content == null && chapters.isEmpty()) throw Error("Missing 'content' or 'chapters'")
        return content?.let { Chapter(title!!, it) } ?: Chapter(title!!, chapters)
    }
}