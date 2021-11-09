package xyz.fragbots.sandboxcore.utils

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.apache.commons.codec.binary.Base64
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.BlockIterator
import xyz.fragbots.sandboxcore.SandboxCore
import xyz.fragbots.sandboxcore.items.SkyblockConsts
import xyz.fragbots.sandboxcore.utils.Reflections.getField
import java.util.*


/*
    * Main utlity class for the sandbox core.
    * A lot of code taken from: https://github.com/KingRainbow44/SkyblockSandbox/blob/main/src/main/java/tk/skyblocksandbox/skyblocksandbox/util/Utility.java
 */
object Utils {

    val base64 = Base64()

    fun rarityToColor(rarity: Int): ChatColor {
        return when (rarity) {
            SkyblockConsts.NONE, SkyblockConsts.COMMON -> ChatColor.WHITE
            SkyblockConsts.UNCOMMON -> ChatColor.GREEN
            SkyblockConsts.RARE -> ChatColor.BLUE
            SkyblockConsts.EPIC -> ChatColor.DARK_PURPLE
            SkyblockConsts.LEGENDARY -> ChatColor.GOLD
            SkyblockConsts.MYTHIC, SkyblockConsts.PURPLE -> ChatColor.LIGHT_PURPLE
            SkyblockConsts.SUPREME -> ChatColor.DARK_RED
            SkyblockConsts.SPECIAL, SkyblockConsts.VERY_SPECIAL -> ChatColor.RED
            else -> ChatColor.WHITE
        }
    }

    fun rarityToString(rarity: Int): String {
        return when (rarity) {
            SkyblockConsts.COMMON -> format("${rarityToColor(rarity)}&lCOMMON")
            SkyblockConsts.UNCOMMON -> format("${rarityToColor(rarity)}&lUNCOMMON")
            SkyblockConsts.RARE -> format("${rarityToColor(rarity)}&lRARE")
            SkyblockConsts.EPIC -> format("${rarityToColor(rarity)}&lEPIC")
            SkyblockConsts.LEGENDARY -> format("${rarityToColor(rarity)}&lLEGENDARY")
            SkyblockConsts.MYTHIC -> format("${rarityToColor(rarity)}&lMYTHIC")
            SkyblockConsts.SUPREME -> format("${rarityToColor(rarity)}&lSUPREME")
            SkyblockConsts.SPECIAL -> format("${rarityToColor(rarity)}&lSPECIAL")
            SkyblockConsts.VERY_SPECIAL -> format("${rarityToColor(rarity)}&lVERY SPECIAL")
            else -> format("${rarityToColor(rarity)}&lCOMMON")
        }
    }

    fun itemTypeToString(itemType: Int): String {
        return when (itemType) {
            SkyblockConsts.BREWING_INGREDIENT, SkyblockConsts.PET, SkyblockConsts.OTHER -> ""
            SkyblockConsts.ITEM -> "ITEM"
            SkyblockConsts.SWORD -> "SWORD"
            SkyblockConsts.BOW -> "BOW"
            SkyblockConsts.PICKAXE -> "PICKAXE"
            SkyblockConsts.AXE -> "AXE"
            SkyblockConsts.SHOVEL -> "SHOVEL"
            SkyblockConsts.HOE -> "HOE"
            SkyblockConsts.SHEARS -> "SHEARS"
            SkyblockConsts.ACCESSORY -> "ACCESSORY"
            SkyblockConsts.REFORGE_STONE -> "REFORGE STONE"
            SkyblockConsts.FISHING_ROD -> "FISHING ROD"
            SkyblockConsts.HELMET -> "HELMET"
            SkyblockConsts.CHESTPLATE -> "CHESTPLATE"
            SkyblockConsts.LEGGINGS -> "LEGGINGS"
            SkyblockConsts.BOOTS -> "BOOTS"
            else -> ""
        }
    }

    fun generateRandomNumber(min: Int, max: Int): Int {
        return Math.round(Math.floor(Math.random() * (max - min + 1) + min)).toInt()
    }

    fun createHologram(loc: Location, name: String): ArmorStand {
        val hologram: ArmorStand = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND) as ArmorStand
        hologram.setGravity(false)
        hologram.canPickupItems = false
        hologram.customName = name
        hologram.isCustomNameVisible = true
        hologram.isVisible = false
        hologram.isMarker = true
        return hologram
    }

    fun createHologramAndDelete(loc: Location,name: String,time:Long){
        val holo = createHologram(loc,name)
        Bukkit.getScheduler().runTaskLater(SandboxCore.instance,{
            holo.remove()
        },time)
    }

    fun formatNumber(number: Number): String {
        return  "%,d".format(number)
    }

    /**
     * Raycasts `distance` blocks ahead and returns farthest non-solid block.
     * Infix, cus it look fancy
     * @author maxus
     */
    fun LivingEntity.raycast(distance: Int) : Location {
        return try {
            val eyes: Location = eyeLocation;
            val iterator = BlockIterator(location, 1.0, distance)
            while (iterator.hasNext()) {
                val loc = iterator.next().location
                if (loc.block.type.isSolid) {
                    if (loc == location) return location
                    loc.pitch = eyes.pitch
                    loc.yaw = eyes.yaw
                    loc.y = loc.y + 1
                    return loc
                }
            }
            val n: Location = eyeLocation.clone().add(eyeLocation.direction.multiply(distance))
            n.pitch = eyes.pitch
            n.yaw = eyes.yaw
            n.y = n.y + 1
            n
        } catch (e: IllegalStateException) {
            return location
        }
    }

    fun f(message: String) : String {
        return format(message)
    }

    fun format(message: String): String {
        return ChatColor.translateAlternateColorCodes('&',message)
    }

    fun getCustomSkull(url: String): ItemStack {
        val profile = GameProfile(UUID.randomUUID(), null)
        val propertyMap = profile.properties
            ?: throw IllegalStateException("Profile doesn't contain a property map")
        val encodedData: ByteArray = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}",
            "http://textures.minecraft.net/texture/$url"
        ).toByteArray())
        propertyMap.put("textures", Property("textures", String(encodedData)))
        val head = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        val headMeta: ItemMeta = head.getItemMeta()
        val headMetaClass: Class<*> = headMeta.javaClass
        getField(headMetaClass, "profile", GameProfile::class.java)[headMeta] = profile
        head.itemMeta = headMeta
        return head
    }
}