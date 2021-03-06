package com.tonyjhuang.fblive.ui.discovery.dummy

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, DummyItem> = HashMap()

    private val COUNT = 5

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): DummyItem {
        return DummyItem(
            position.toString(),
            "https://fiverr-res.cloudinary.com/images/t_main1,q_auto,f_auto,q_auto,f_auto/gigs/116078775/original/518584d3f43543d933d1ea38af932500dcff941f/design-a-awesome-thumbnail-for-you-youtube-stream.png",
            "the bestest stream 🔥",
            "chauncy the rapper")
    }


    /**
     * A dummy item representing a piece of content.
     */
    data class DummyItem(val id: String, val thumbnail: String, val streamName: String, val creatorName: String)
}
