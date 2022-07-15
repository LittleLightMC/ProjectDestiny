package pro.darc.projectm.services.moderator

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.scoreboard.Team
import pro.darc.projectm.ProjectMCoreMain
import pro.darc.projectm.extension.*
import java.io.File
import java.util.UUID

enum class TeamMark(val id: Int) {
    TEAM_NONE(-1),
    TEAM_START(0),
    TEAM_JUNGLE(1),
    TEAM_ICE(2),
    TEAM_DESERT(3),
    TEAM_PLAIN(4),
    TEAM_JAIL(5),
}

data class PlayerTeamInfo (
    val uuid: UUID,
    val team: TeamMark
)

class TeamService: Listener,
    PacketAdapter(
        ProjectMCoreMain.instance,
        ListenerPriority.HIGH,
        PacketType.Play.Server.NAMED_ENTITY_SPAWN
    ) {

    private val playerTeamInfo: MutableList<PlayerTeamInfo> = mutableListOf()
    private val protocolManager = ProtocolLibrary.getProtocolManager()
    private val minecraftTeam: Map<Int, Team>
    var needInit: Boolean = true
        private set

    init {
        protocolManager.addPacketListener(this)
        for (team in TeamMark.values()) {
            scoreboardManager.mainScoreboard.getTeam(team.name)?.unregister()
        }
        minecraftTeam = mapOf(
            Pair(TeamMark.TEAM_JUNGLE.id, scoreboardManager.mainScoreboard.registerNewTeam(TeamMark.TEAM_JUNGLE.name).apply {
                prefix("丛林| ".toComponent().withColor(0x00CD66))
            }),
            Pair(TeamMark.TEAM_ICE.id, scoreboardManager.mainScoreboard.registerNewTeam(TeamMark.TEAM_ICE.name).apply {
                prefix("冰原| ".toComponent().withColor(0x87CEFA))
            }),
            Pair(TeamMark.TEAM_DESERT.id, scoreboardManager.mainScoreboard.registerNewTeam(TeamMark.TEAM_DESERT.name).apply {
                prefix("沙漠| ".toComponent().withColor(0xCDBE70))
            }),
            Pair(TeamMark.TEAM_PLAIN.id, scoreboardManager.mainScoreboard.registerNewTeam(TeamMark.TEAM_PLAIN.name).apply {
                prefix("平原| ".toComponent().withColor(0x00BFFF))
            }),
            Pair(TeamMark.TEAM_JAIL.id, scoreboardManager.mainScoreboard.registerNewTeam(TeamMark.TEAM_JAIL.name).apply {
                prefix("流放| ".toComponent())
            }),
        )
        loadFromFile()
    }

    fun makeTeam() {
        val players = onlinePlayers.filter { player -> !player.isStuff }.toMutableList()
        val eachTeamNum = onlinePlayers.size / 4
        for (team in TeamMark.TEAM_PLAIN.id downTo TeamMark.TEAM_JUNGLE.id) {
            for (i in eachTeamNum downTo 1) {
                val randomIndex = players.randomIndex()
                val player = players[randomIndex]
                val teamInfo = PlayerTeamInfo(
                    player.uniqueId,
                    TeamMark.values().first { it.id == team }
                )
                playerTeamInfo.add(teamInfo)

                minecraftTeam[teamInfo.team.id]?.addPlayer(player)

                players.removeAt(randomIndex)
            }
        }
        for (player in players) {
            val randomTeam = TeamMark.values().filter { it.id >= TeamMark.TEAM_JUNGLE.id && it.id <= TeamMark.TEAM_PLAIN.id }.random()
            playerTeamInfo.add(
                PlayerTeamInfo(
                    player.uniqueId,
                    randomTeam,
                )
            )
            minecraftTeam[randomTeam.id]?.addPlayer(player)
        }
        saveToFile()
        needInit = false
    }

    private fun saveToFile() {
        val targetFile = File(plugin.dataFolder, "team.csv")
        var text = ""
        for (i in playerTeamInfo) {
            text += "${i.uuid},${i.team.name}\n"
        }
        targetFile.writeText(text)
    }

    private fun loadFromFile() {
        val targetFile = File(plugin.dataFolder, "team.csv")
        if (!targetFile.exists()) return
        val lines = targetFile.readLines()
        for (line in lines) {
            val s = line.split(',')
            val playerInfo = PlayerTeamInfo(UUID.fromString(s[0]), TeamMark.valueOf(s[1]))
            playerTeamInfo.add(playerInfo)
            minecraftTeam[playerInfo.team.id]?.addPlayer(Bukkit.getOfflinePlayer(playerInfo.uuid))
        }
    }

    fun getPlayerInfo(): List<PlayerTeamInfo> = playerTeamInfo

    fun getPlayerTeam(player: Player): TeamMark = playerTeamInfo.firstOrNull { it.uuid == player.uniqueId }?.team ?: TeamMark.TEAM_NONE

    override fun onPacketSending(event: PacketEvent?) {
    }
}