package xyz.fragbots.sandboxcore.items.weapons

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import xyz.fragbots.sandboxcore.SandboxCore
import xyz.fragbots.sandboxcore.items.*
import xyz.fragbots.sandboxcore.utils.LoreGenerator
import xyz.fragbots.sandboxcore.utils.player.PlayerExtensions.getNearbySkyblockEntities
import xyz.fragbots.sandboxcore.utils.player.PlayerStats
import kotlin.math.abs

class YetiSword : SkyblockItem(Material.IRON_SWORD, "Yeti Sword", SkyblockItemIDS.YETISWORD) {
    override var ability1: SkyblockItemAbility? = SkyblockItemAbility(
        "Terrain Toss","&6Item Ability: Terrain Toss &e&lRIGHT CLICK",
        "&7Throws a chunk of terrain in the\n" +
                "&7direction you are facing! Deals\n" +
                "&7up to &c%%dmg%% &7damage.",
        250,
        1, 15000, 0.3
    )

    override fun getLore(playerStats: PlayerStats, itemStack: ItemStack?): Collection<String> {
        return LoreGenerator() //It has no
            .generic(getItemData(playerStats, false, itemStack), playerStats, this)
    }

    override fun getDefaultData(playerStats: PlayerStats): SkyblockItemData {
        val data = SkyblockItemData(id)

        data.rarity = SkyblockConsts.LEGENDARY
        data.itemType = SkyblockConsts.SWORD
        data.reforgeable = true
        data.dungeonitem = false

        data.baseDamage = 150
        data.baseStrength = 170
        data.baseIntel = 3000

        return data;
    }

    override fun abilityUse(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action
        if(action!= Action.RIGHT_CLICK_AIR) {
            return
        }

        val armorStands = ArrayList<ArmorStand>()
        val landingLoc = player.getTargetBlock(null as Set<Material?>?, 30).location
        if(landingLoc.block!=null){
            landingLoc.add(Vector(0.0,1.1,0.0))
        }
        val loc1 = player.eyeLocation.add(Vector(0.0,0.1,0.0))
        val loc2 = landingLoc
        val path = parabola(loc1,loc2,18)
        val locationIterator = path.iterator()

        //Bottom Layer
        addStand(loc1,player.location,0,0,0,armorStands)
        addStand(loc1,player.location,1,0,0,armorStands)
        addStand(loc1,player.location,-1,0,0,armorStands)
        addStand(loc1,player.location,0,0,1,armorStands)
        addStand(loc1,player.location,0,0,-1,armorStands)

        //Top Layer
        addStand(loc1,player.location,0,1,0,armorStands)
        addStand(loc1,player.location,1,1,0,armorStands)
        addStand(loc1,player.location,-1,1,0,armorStands)
        addStand(loc1,player.location,2,1,0,armorStands)
        addStand(loc1,player.location,-2,1,0,armorStands)
        addStand(loc1,player.location,0,1,1,armorStands)
        addStand(loc1,player.location,0,1,-1,armorStands)
        addStand(loc1,player.location,0,1,2,armorStands)
        addStand(loc1,player.location,0,1,-2,armorStands)
        addStand(loc1,player.location,1,1,1,armorStands)
        addStand(loc1,player.location,-1,1,-1,armorStands)
        addStand(loc1,player.location,-1,1,1,armorStands)
        addStand(loc1,player.location,1,1,-1,armorStands)

        object : BukkitRunnable() {
            override fun run() {
                if(locationIterator.hasNext()){
                    moveStands(armorStands,locationIterator.next())
                }else{
                    loc1.world.createExplosion(armorStands[0].location.x,armorStands[0].location.y,armorStands[0].location.z,5f,false,false)
                    val nearbyEntities = armorStands[0].location.getNearbySkyblockEntities(5.0,5.0,5.0)
                    var totalDamage:Long = 0
                    for(entity in nearbyEntities){
                        totalDamage+=SandboxCore.instance.damageExecutor.executePVEMagic(player,entity,ability1!!)
                    }
                    for(stand in armorStands){
                        try{
                            stand.passenger.remove()
                        }catch (ignored:NullPointerException){}
                        stand.remove()
                    }
                    cancel()
                }
            }
        }.runTaskTimer(SandboxCore.instance,0,1)
    }

    fun addStand(baseLoc: Location, playerLoc:Location, xChange:Int, yChange:Int, zChange:Int, arrayList:ArrayList<ArmorStand>){
        val blockLoc = playerLoc.add(0.0,-2.0,0.0).add(Vector(xChange,yChange,zChange))
        val block: ItemStack = if(blockLoc.block!=null&&blockLoc.block.type!=Material.AIR){
            blockLoc.block.state.data.toItemStack()
        }else{
            ItemStack(Material.STAINED_GLASS,1,3.toShort())
        }
        arrayList.add(createFloatingBlock(baseLoc.clone().add(Vector(xChange,yChange,zChange)),block))
    }
    fun moveStands(stands:List<ArmorStand>, moveTo:Location){
        val mainStand = stands.get(0)
        val xMove = moveTo.x-mainStand.location.x
        val yMove = moveTo.y-mainStand.location.y
        val zMove = moveTo.z-mainStand.location.z
        for(stand in stands){
            try {
                val passenger: Entity = stand.passenger
                stand.eject()
                stand.teleport(stand.location.add(xMove, yMove, zMove))
                stand.passenger = passenger
            }catch (ignored:NullPointerException){}
        }
    }
    fun createFloatingBlock(loc: Location,item: ItemStack):ArmorStand{
        val hologram: ArmorStand = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND) as ArmorStand
        hologram.setGravity(false)
        hologram.canPickupItems = false
        hologram.isVisible = false
        hologram.isMarker = true
        val fallingBlock = loc.world.spawnFallingBlock(loc,item.type,item.durability.toByte())
        fallingBlock.dropItem = false
        hologram.passenger = fallingBlock
        return hologram
    }
    fun parabola(loc1:Location, loc2:Location,points:Int):List<Location>{
        val locList = ArrayList<Location>()
        val distance = loc1.distance(loc2)
        for(i in 1 until points){
            val ratio = i.toDouble()/points
            val location = loc1.lerp(loc2, ratio)
            val distanceToMid = abs(ratio-0.5)
            val additionalHeight = ((-(4.0*(distance/3.0)))*(distanceToMid*distanceToMid))+(distance/3.0)
            location.add(0.0, additionalHeight,0.0)
            locList.add(location)
        }
        return locList
    }
    fun lerp(point1:Double,point2:Double,alpha:Double):Double{
        return point1 + alpha * (point2 - point1);
    }
    fun Location.lerp(loc2: Location, alpha: Double): Location {
        val xLerp = lerp(x,loc2.x,alpha)
        val yLerp = lerp(y,loc2.y,alpha)
        val zLerp = lerp(z,loc2.z,alpha)
        return Location(world,xLerp,yLerp,zLerp)
    }
}