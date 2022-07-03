package pro.darc.projectm

import org.bukkit.plugin.java.JavaPlugin
import pro.darc.projectm.services.moderator.ModeratorService
import pro.darc.projectm.services.social.SocialService

class ProjectMCoreMain : JavaPlugin() {

    init {
        instance = this
    }

    override fun onEnable() {
        socialService = SocialService()
        moderatorService = ModeratorService()
    }

    companion object {
        lateinit var instance: ProjectMCoreMain

        lateinit var socialService: SocialService
        lateinit var moderatorService: ModeratorService
    }
}