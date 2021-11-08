package xyz.fragbots.sandboxcore.items.misc

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.fragbots.sandboxcore.items.SkyblockConsts
import xyz.fragbots.sandboxcore.items.SkyblockItem
import xyz.fragbots.sandboxcore.items.SkyblockItemData
import xyz.fragbots.sandboxcore.items.SkyblockItemIDS
import xyz.fragbots.sandboxcore.utils.Utils
import xyz.fragbots.sandboxcore.utils.player.PlayerStats

class RemnantEye : SkyblockItem(Material.SKULL_ITEM, "Remnant of the Eye", SkyblockItemIDS.REMNANTEYE) {

    override val isHead = true
    override val headTextureUrl = "7d389c55ecf7db572d6961ce3d57b572e761397b67a2d6d94c72fc91dddd74"

    override fun getLore(playerStats: PlayerStats, itemStack: ItemStack?): Collection<String> {
        return listOf(
            Utils.f("&7Keeps you alive when you are on"),
            Utils.f("&7death's door, granting a short"),
            Utils.f("&7period of invincibility"),
            Utils.f("&7Consumed on use."),
            Utils.f(""),
            Utils.f("&cNOTE: ONLY WORKS ON THE END"),
            Utils.f("&cISLAND&7."),
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