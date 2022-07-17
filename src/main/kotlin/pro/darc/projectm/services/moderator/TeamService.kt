package pro.darc.projectm.services.moderator

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Team
import pro.darc.projectm.ProjectMCoreMain
import pro.darc.projectm.dsl.Log
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
    var team: TeamMark
)

class TeamService: Listener,
    PacketAdapter(
        ProjectMCoreMain.instance,
        ListenerPriority.HIGH,
        PacketType.Play.Server.NAMED_ENTITY_SPAWN,
        PacketType.Play.Server.PLAYER_INFO,
    ) {

    private val playerTeamInfo: MutableList<PlayerTeamInfo> = mutableListOf()
    private val protocolManager = ProtocolLibrary.getProtocolManager()
    private lateinit var minecraftTeam: Map<Int, Team>
    var needInit: Boolean = true
        private set

    init {
        protocolManager.addPacketListener(this)
        initMinecraftTeam()
    }

    private fun initMinecraftTeam() {
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
    }

    fun makeTeam() {
        initMinecraftTeam()
        playerTeamInfo.clear()
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

    fun loadFromFile() {
        val targetFile = File(plugin.dataFolder, "team.csv")
        if (!targetFile.exists()) return
        initMinecraftTeam()
        playerTeamInfo.clear()
        val lines = targetFile.readLines()
        for (line in lines) {
            val s = line.split(',')
            try {
                val playerInfo = PlayerTeamInfo( UUID.fromString(s[0]), TeamMark.valueOf(s[1]))
                playerTeamInfo.add(playerInfo)
                Bukkit.getOfflinePlayer(playerInfo.uuid).let { it.name?.let { _ -> minecraftTeam[playerInfo.team.id]?.addPlayer(it) } }
            } catch (e: java.lang.IllegalArgumentException) {
                Log.warning("导入${line}的时候发生了错误(大概率是UUID格式问题)")
            }
        }
        needInit = false
    }

    fun getPlayerInfo(): List<PlayerTeamInfo> = playerTeamInfo

    fun getPlayerTeam(player: Player): TeamMark = playerTeamInfo.firstOrNull { it.uuid == player.uniqueId }?.team ?: TeamMark.TEAM_NONE

    fun setPlayerTeam(player: Player, team: TeamMark) {
        var playerInfo = playerTeamInfo.firstOrNull { it.uuid == player.uniqueId }
        if (playerInfo == null) {
            playerInfo = PlayerTeamInfo(player.uniqueId, team)
            playerTeamInfo.add(playerInfo)
        }
        scoreboardManager.mainScoreboard.getPlayerTeam(player)?.removePlayer(player)
        minecraftTeam[playerInfo.team.id]?.addPlayer(player)
    }

    override fun onPacketSending(event: PacketEvent) {
        val packet = event.packet
        when (event.packetType) {
            PacketType.Play.Server.NAMED_ENTITY_SPAWN -> {}
            PacketType.Play.Server.PLAYER_INFO -> {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun evtPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
        val team = getPlayerTeam(event.player)
        if (team != TeamMark.TEAM_NONE) {
            minecraftTeam[team.id]?.addPlayer(event.player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtPlayerModeChange(event: PlayerGameModeChangeEvent) {
        if (event.newGameMode != GameMode.SPECTATOR && event.player.isStuff && !event.player.isOperator) {
            event.isCancelled = true
            event.player.sendMessage("${event.cause}将你设为了${event.newGameMode}, 此行为已被阻止".toComponent().withSuccessColor().withPrefix())
        }
    }
}