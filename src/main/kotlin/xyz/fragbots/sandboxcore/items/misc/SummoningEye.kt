package xyz.fragbots.sandboxcore.items.misc

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.fragbots.sandboxcore.items.SkyblockConsts
import xyz.fragbots.sandboxcore.items.SkyblockItem
import xyz.fragbots.sandboxcore.items.SkyblockItemData
import xyz.fragbots.sandboxcore.items.SkyblockItemIDS
import xyz.fragbots.sandboxcore.utils.Utils
import xyz.fragbots.sandboxcore.utils.player.PlayerStats

class SummoningEye : SkyblockItem(Material.SKULL_ITEM, "Summoning Eye", SkyblockItemIDS.SUMMONINGEYE) {

    override val isHead = true
    override val headTextureUrl = "daa8fc8de6417b48d48c80b443cf5326e3d9da4dbe9b25fcd49549d96168fc0"

    override fun getLore(playerStats: PlayerStats, itemStack: ItemStack?): Collection<String> {
        return listOf(
            Utils.f("&7Use this at the &5Ender Altar"),
            Utils.f("&7in the &5Dragon's Nest&7 to"),
            Utils.f("&7summon Ender Dragons!"),
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