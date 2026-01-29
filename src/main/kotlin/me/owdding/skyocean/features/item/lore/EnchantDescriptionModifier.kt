package me.owdding.skyocean.features.item.lore

import me.owdding.skyocean.config.features.lorecleanup.LoreModifierConfig
import me.owdding.skyocean.features.item.modifier.AbstractItemModifier
import me.owdding.skyocean.features.item.modifier.ItemModifier
import me.owdding.skyocean.utils.Utils.unaryPlus
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.math.max

@ItemModifier
object EnchantDescriptionModifier : AbstractItemModifier() {

    override val displayName: Component = +"skyocean.config.lore_modifiers.remove_enchant_description"
    override val isEnabled: Boolean get() = LoreModifierConfig.removeEnchantDescription

    private val LEVEL_SUFFIX = Regex("""\s+[IVXLCDM]+$""")
    private val DISPLAY_NAME_TO_ID: Map<String, String> = mapOf(
        "Dragon Tracer" to "AIMING",
        "Woodsplitter" to "ARCANE",
        "Gravity" to "DRAGON_HUNTER",
        "Prismatic" to "PRISTINE",
        "Drain" to "SYPHON",
        "Turbo-Cacti" to "TURBO_CACTUS",
        "Turbo-Cocoa" to "TURBO_COCO",
        "Bank" to "ULTIMATE_BANK",
        "Bobbin' Time" to "ULTIMATE_BOBBIN_TIME",
        "Chimera" to "ULTIMATE_CHIMERA",
        "Combo" to "ULTIMATE_COMBO",
        "Crop Fever" to "ULTIMATE_CROP_FEVER",
        "Fatal Tempo" to "ULTIMATE_FATAL_TEMPO",
        "First Impression" to "ULTIMATE_FIRST_IMPRESSION",
        "Flash" to "ULTIMATE_FLASH",
        "Flowstate" to "ULTIMATE_FLOWSTATE",
        "Habanero Tactics" to "ULTIMATE_HABANERO_TACTICS",
        "Inferno" to "ULTIMATE_INFERNO",
        "Last Stand" to "ULTIMATE_LAST_STAND",
        "Legion" to "ULTIMATE_LEGION",
        "Missile" to "ULTIMATE_MISSILE",
        "No Pain No Gain" to "ULTIMATE_NO_PAIN_NO_GAIN",
        "One For All" to "ULTIMATE_ONE_FOR_ALL",
        "Refrigerate" to "ULTIMATE_REFRIGERATE",
        "Duplex" to "ULTIMATE_REITERATE",
        "Rend" to "ULTIMATE_REND",
        "Soul Eater" to "ULTIMATE_SOUL_EATER",
        "Swarm" to "ULTIMATE_SWARM",
        "The One" to "ULTIMATE_THE_ONE",
        "Wisdom" to "ULTIMATE_WISDOM",
    )

    override fun appliesTo(itemStack: ItemStack): Boolean {
        val enchantments = itemStack.getData(DataTypes.ENCHANTMENTS) ?: return false
        return enchantments.isNotEmpty()
    }

    override fun modifyTooltip(item: ItemStack, list: MutableList<Component>, previousResult: Result?) = withMerger(list) {
        val enchantments = item.getData(DataTypes.ENCHANTMENTS)!!.keys

        addUntil {
            isEnchantLine(it.stripped, enchantments) && original[max(0, index - 1)].stripped.isBlank()
        }
        copy()

        while (canRead()) {
            val peeked = peek().stripped
            if (peeked.isEmpty()) break

            if (!isEnchantLine(peeked, enchantments)) {
                read()
            } else {
                copy()
            }
        }

        addRemaining()
        Result.modified
    }

    private fun isEnchantLine(string: String, enchantments: Set<String>): Boolean =
        normalizeEnchantKey(string) in enchantments

    private fun normalizeEnchantKey(stripped: String): String =
        displayNameToId(
            stripped
                .replace('’', '\'')
                .replace(LEVEL_SUFFIX, ""),
        )
            .replace(' ', '_')
            .replace('-', '_')
            .lowercase()

    fun displayNameToId(input: String): String = DISPLAY_NAME_TO_ID.getOrDefault(input, input)
}
