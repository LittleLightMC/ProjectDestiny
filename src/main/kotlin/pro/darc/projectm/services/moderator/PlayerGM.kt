package pro.darc.projectm.services.moderator

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import pro.darc.projectm.dsl.ProjectMListener

class PlayerGM : ProjectMListener() {

    init {
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun evtPlayerJoin(event: PlayerJoinEvent) {
    }
}