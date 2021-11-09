package xyz.fragbots.sandboxcore.items.misc

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.fragbots.sandboxcore.items.SkyblockConsts
import xyz.fragbots.sandboxcore.items.SkyblockItem
import xyz.fragbots.sandboxcore.items.SkyblockItemData
import xyz.fragbots.sandboxcore.items.SkyblockItemIDS
import xyz.fragbots.sandboxcore.utils.Utils
import xyz.fragbots.sandboxcore.utils.player.PlayerStats

class SleepingEye : SkyblockItem(Material.SKULL_ITEM, "Sleeping Eye", SkyblockItemIDS.SLEEPINGEYE){
    override val isHead = true
    override val headTextureUrl = "37c0d010dd0e512ffea108d7c5fe69d576c31ec266c884b51ec0b28cc457"

    override fun getLore(playerStats: PlayerStats, itemStack: ItemStack?): Collection<String> {
        return listOf(
            Utils.f("&7Keep this item in your"),
            Utils.f("&7inventory to recover your placed"),
            Utils.f("&7Summoning Eye when you leave or"),
            Utils.f("&7when you click the Ender Altar."),
            Utils.f("&7This item becomes imbued when the"),
            Utils.f("&7dragon spawns, turning it into a"),
            Utils.f("&7Remnant of the Eye."),
            Utils.f(""),
            Utils.f("&5&lEPIC")
        )
    }

    override fun getDefaultData(playerStats: PlayerStats): SkyblockItemData {
        val data = SkyblockItemData(id)

        data.rarity = SkyblockConsts.EPIC
        data.itemType = SkyblockConsts.OTHER

        data.reforgeable = false

        return data
    }
}