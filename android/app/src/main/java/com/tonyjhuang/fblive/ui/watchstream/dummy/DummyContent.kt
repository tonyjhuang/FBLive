package com.tonyjhuang.fblive.ui.watchstream.dummy

import android.util.Log
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    private val sentences = listOf(
        "He stepped gingerly onto the bridge knowing that enchantment awaited on the other side.",
        "It was getting dark, and we weren’t there yet.",
        "Please wait outside of the house.",
        "It was at that moment that he learned there are certain parts of the body that you should never Nair.",
        "Nobody questions who built the pyramids in Mexico.",
        "I may struggle with geography, but I'm sure I'm somewhere around here.",
        "Improve your goldfish's physical fitness by getting him a bicycle.",
        "Yeah, I think it's a good environment for learning English.",
        "There was no ice cream in the freezer, nor did they have money to go to the store.",
        "As the rental car rolled to a stop on the dark road, her fear increased by the moment.",
        "The rusty nail stood erect, angled at a 45-degree angle, just waiting for the perfect barefoot to come along.",
        "He was the type of guy who liked Christmas lights on his house in the middle of July.",
        "Three years later, the coffin was still full of Jello.",
        "He told us a very exciting adventure story.",
        "For oil spots on the floor, nothing beats parking a motorbike in the lounge.",
        "The knives were out and she was sharpening hers.",
        "He used to get confused between soldiers and shoulders, but as a military man, he now soldiers responsibility.",
        "If you like tuna and tomato sauce- try combining the two. It’s really not as bad as it sounds.",
        "She finally understood that grief was her love with no place for it to go.",
        "My dentist tells me that chewing bricks is very bad for your teeth.",
        "We have a lot of rain in June.",
        "The toy brought back fond memories of being lost in the rain forest.",
        "It's not often you find a soggy banana on the street.",
        "If you don't like toenails, you probably shouldn't look at your feet.",
        "She is never happy until she finds something to be unhappy about; then, she is overjoyed.",
        "He took one look at what was under the table and noped the hell out of there.",
        "The efficiency we have at removing trash has made creating trash more acceptable.",
        "Jeanne wished she has chosen the red button.",
        "The minute she landed she understood the reason this was a fly-over state.",
        "Carol drank the blood as if she were a vampire.",
        "He decided that the time had come to be stronger than any of the excuses he'd used until then.",
        "He invested some skill points in Charisma and Strength.",
        "The Guinea fowl flies through the air with all the grace of a turtle.",
        "We have never been to Asia, nor have we visited Africa.",
        "Nothing is as cautiously cuddly as a pet porcupine.",
        "She lived on Monkey Jungle Road and that seemed to explain all of her strangeness.",
        "He figured a few sticks of dynamite were easier than a fishing pole to catch fish.",
        "I like to leave work after my eight-hour tea-break.",
        "I'm confused: when people ask me what's up, and I point, they groan.",
        "Three generations with six decades of life experience.",
        "Happiness can be found in the depths of chocolate pudding.",
        "Behind the window was a reflection that only instilled fear.",
        "The three-year-old girl ran down the beach as the kite flew behind her.",
        "She had the gift of being able to paint songs.",
        "She used her own hair in the soup to give it more flavor.",
        "He said he was not there yesterday; however, many people saw him there.",
        "You realize you're not alone as you sit in your bedroom massaging your calves after a long day of playing tug-of-war with Grandpa Joe in the hospital.",
        "I am my aunt's sister's daughter.",
        "The river stole the gods.",
        "Two more days and all his problems would be solved.")

    private val authors = listOf(
        "Nigel Ellis",
        "Tarik East",
        "Izabella Dunlop",
        "Esmai Fraser",
        "Devin Kent",
        "Ayyan Sparks",
        "Farrell Huber",
        "Ammaar Downs",
        "Pearl Dougherty",
        "Ryker Booth",
        "Burhan Sweet",
        "Gabriel Mccormack",
        "Nicolle Hollis",
        "Allan Legge",
        "Ayat Rose",
        "Haseeb Webb",
        "Ashanti Kaur",
        "Imani Abbott",
        "Maia Mellor",
        "Iain Mckeown",
        "Amber Sadler",
        "Lacy Nielsen",
        "Brody Britton",
        "Josiah Howells",
        "Aurelia Short",
        "Dione Duffy",
        "Mysha Milne",
        "Emillie Glover",
        "Codey Glass",
        "Kacper Robertson",
        "Ted Serrano",
        "Blair Terry",
        "Kunal Regan",
        "Asma Weston",
        "Arda Davie",
        "Subhaan Elliott",
        "Loui Rennie",
        "Marcelina Wilkins",
        "Sadie Marin",
        "Michelle Travis",
        "Efan Barker",
        "Riaz Sims",
        "Isobel Bravo",
        "Colton Redfern",
        "Frederick Sanchez",
        "Shiloh Goodman",
        "Casper Ferreira",
        "Kiefer Anthony",
        "Sonny Beach",
        "Kelsey Grant"
    )
    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, DummyItem> = HashMap()



    private val COUNT = 25

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
        Log.d("fbl---", "${authors.size} ${sentences.size}")
        return DummyItem(position.toString(), authors.random(), sentences.random())
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class DummyItem(val id: String, val authorName: String, val body: String)
}
