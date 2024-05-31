package xyz.fragbots.sandboxcore.entitites

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

class SkyblockEntityManager {
    val entities = HashMap<Int,SkyblockEntity>();

    fun registerEntity(sbEntity: SkyblockEntity):Int {
        val entity = sbEntity.entity ?: return -1
        entities[entity.entityId] = sbEntity
        return entity.entityId
    }

    fun unRegisterEntity(id:Int){
        entities.remove(id)
    }

    fun isSkyblockEntity(entity: Entity):Boolean{
        if(entity !is LivingEntity) return false
        return entities.containsKey(entity.entityId)
    }

    fun getSkyblockEntity(entity: LivingEntity):SkyblockEntity? {
        return entities[entity.entityId]
    }

    fun killAll() {
        for(entity in entities) {
            entity.value.entity?.remove()
        }
        entities.clear()
    }
}