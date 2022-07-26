package pro.darc.projectm.extension

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player

val pManager: ProtocolManager
    get() = ProtocolLibrary.getProtocolManager()

fun ProtocolManager.sendServerPacket(players: List<Player>, packet: PacketContainer) = players.forEach {
    pManager.sendServerPacket(it, packet)
}
