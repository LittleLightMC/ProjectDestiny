package pro.darc.projectm.dsl.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import pro.darc.projectm.dsl.menu.slot.Slot
import java.util.*

interface Menu<T : Slot> : InventoryHolder {

    var title: String
    var lines: Int
    var cancelOnClick: Boolean

    val viewers: Map<Player, Inventory>
    val slots: TreeMap<Int, T>

    val data: WeakHashMap<String, Any>
    val playerData: WeakHashMap<Player, WeakHashMap<String, Any>>

    val eventHandler: MenuEventHandler

    var baseSlot: T
    var updateDelay: Long

    fun setSlot(slot: Int, slotObj: T)

    fun update(players: Set<Player> = viewers.keys)
    fun updateSlot(slot: Slot, players: Set<Player> = viewers.keys)

    fun update(vararg players: Player) = update(players.toSet())
    fun updateSlot(slot: Slot, vararg players: Player) = updateSlot(slot, players.toSet())

    fun openToPlayer(vararg players: Player)

    fun clearData() {
        data.clear()
        for(slot in slotsWithBaseSlot())
            slot.clearSlotData()
    }

    fun clearPlayerData(player: Player) {
        playerData.remove(player)
        for(slot in slotsWithBaseSlot())
            slot.clearPlayerData(player)
    }

    fun close(player: Player, closeInventory: Boolean = true)
}
