package pro.darc.projectm.dsl.menu.dsl.pagination

import org.bukkit.entity.Player
import pro.darc.projectm.dsl.menu.MenuPlayer
import pro.darc.projectm.dsl.menu.MenuPlayerInventory
import pro.darc.projectm.dsl.menu.dsl.MenuDSL
import pro.darc.projectm.dsl.menu.dsl.pagination.slot.PaginationSlotDSL
import pro.darc.projectm.dsl.menu.dsl.slot.SlotDSL
import pro.darc.projectm.utils.collection.ObservableCollection
import java.lang.IllegalArgumentException
import java.util.*

typealias ItemsProvider<T> = () -> ObservableCollection<T>
typealias ItemsAdapter<T> = MenuPlayer.(List<T>) -> List<T>

enum class Orientation { HORIZONTAL, VERTICAL }

inline fun <T> MenuPagination<T>.slot(
    builder: PaginationSlotDSL<T>.() -> Unit
) {
    for (paginationSlot in paginationSlots.values) {
        paginationSlot.builder()
    }
}

fun MenuDSL.setPlayerOpenPage(player: Player, page: Int) {
    playerData.put(
        player,
        WeakHashMap<String, Any>().apply { put(PAGINATION_OPEN_PAGE_KEY, page) }
    )
}
inline fun <T> MenuDSL.pagination(
    itemsProvider: ObservableCollection<T>,
    nextPageSlot: SlotDSL,
    previousPageSlot: SlotDSL,
    autoUpdateSwitchPageSlot: Boolean = true,
    startLine: Int = 1,
    endLine: Int = lines-1,
    startSlot: Int = 1,
    endSlot: Int = 9,
    orientation: Orientation = Orientation.HORIZONTAL,
    noinline itemsAdapterOnOpen: ItemsAdapter<T>? = null,
    noinline itemsAdapterOnUpdate: ItemsAdapter<T>? = null,
    builder: MenuPaginationImpl<T>.() -> Unit
): MenuPaginationImpl<T> {
    return pagination(
        { itemsProvider },
        nextPageSlot,
        previousPageSlot,
        autoUpdateSwitchPageSlot,
        startLine,
        endLine,
        startSlot,
        endSlot,
        orientation,
        itemsAdapterOnOpen,
        itemsAdapterOnUpdate,
        builder
    )
}

inline fun <T> MenuDSL.pagination(
    noinline itemsProvider: ItemsProvider<T>,
    nextPageSlot: SlotDSL,
    previousPageSlot: SlotDSL,
    autoUpdateSwitchPageSlot: Boolean = true,
    startLine: Int = 1,
    endLine: Int = lines-1,
    startSlot: Int = 1,
    endSlot: Int = 9,
    orientation: Orientation = Orientation.HORIZONTAL,
    noinline itemsAdapterOnOpen: ItemsAdapter<T>? = null,
    noinline itemsAdapterOnUpdate: ItemsAdapter<T>? = null,
    builder: MenuPaginationImpl<T>.() -> Unit
): MenuPaginationImpl<T> {
    if(startSlot > endSlot) throw IllegalArgumentException()
    if(startLine > endLine) throw IllegalArgumentException()

    return MenuPaginationImpl(
        this,
        itemsProvider,
        nextPageSlot,
        previousPageSlot,
        autoUpdateSwitchPageSlot,
        startLine,
        endLine,
        startSlot,
        endSlot,
        orientation,
        itemsAdapterOnOpen,
        itemsAdapterOnUpdate
    ).apply(builder)
}

interface MenuPagination<T> {
    val menu: MenuDSL
    val paginationSlots: TreeMap<Int, PaginationSlotDSL<T>>
    val paginationEventHandler: PaginationEventHandler

    val itemsProvider: ItemsProvider<T>

    val nextPageSlot: SlotDSL
    val previousPageSlot: SlotDSL

    val autoUpdateSwitchPageSlot: Boolean

    val startLine: Int
    val endLine: Int

    val startSlot: Int
    val endSlot: Int

    val orientation: Orientation

    val itemsAdapterOnOpen: ItemsAdapter<T>?
    val itemsAdapterOnUpdate: ItemsAdapter<T>?

    fun onPageChange(pageChange: MenuPlayerPageChangeEvent) {
        paginationEventHandler.pageChangeCallbacks.add(pageChange)
    }

    fun onPageAvailable(pageAvailable: MenuPlayerPageAvailableEvent) {
        paginationEventHandler.pageAvailableCallbacks.add(pageAvailable)
    }

    fun hasPreviousPage(player: Player): Boolean

    fun hasNextPage(player: Player): Boolean

    fun getPlayerCurrentPage(player: Player): Int

    fun updateItemsToPlayer(menuPlayer: MenuPlayerInventory)
}
