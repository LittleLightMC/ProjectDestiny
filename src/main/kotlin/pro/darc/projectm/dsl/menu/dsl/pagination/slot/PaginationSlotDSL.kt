package pro.darc.projectm.dsl.menu.dsl.pagination.slot

import org.bukkit.entity.Player
import pro.darc.projectm.dsl.menu.dsl.slot.SlotDSL
import java.util.*

interface PaginationSlotDSL<T> {
    val slotRoot: SlotDSL

    val paginationEventHandler: PaginationSlotEventHandler<T>

    val slotData: WeakHashMap<String, Any>
    val playerSlotData: WeakHashMap<Player, WeakHashMap<String, Any>>

    /**
     * Cancel the interaction with this slot
     */
    var cancel: Boolean

    fun onPageChange(pageChange: MenuPlayerSlotPageChangeEvent<T>) {
        paginationEventHandler.pageChangeCallbacks.add(pageChange)
    }

    fun onClick(click: MenuPlayerPageSlotInteractEvent<T>) {
        paginationEventHandler.interactCallbacks.add(click)
    }

    fun onRender(render: MenuPlayerPageSlotRenderEvent<T>) {
        paginationEventHandler.renderCallbacks.add(render)
    }

    fun onUpdate(update: MenuPlayerPageSlotUpdateEvent<T>) {
        paginationEventHandler.updateCallbacks.add(update)
    }
}