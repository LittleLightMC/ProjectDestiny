package pro.darc.projectm

import org.bukkit.plugin.java.JavaPlugin
import pro.darc.projectm.provider.conf.WithConfiguration
import pro.darc.projectm.provider.conf.getDefaultConfiguration
import pro.darc.projectm.provider.conf.models.GlobalConfig
import pro.darc.projectm.provider.conf.saveConfigurationDefaultPath
import pro.darc.projectm.services.social.SocialService

class ProjectMCoreMain : JavaPlugin(), WithConfiguration {

    init {
        instance = this
    }

    override fun onEnable() {
        dataFolder.mkdirs()

        socialService = SocialService()
        globalConfig = getDefaultConfiguration()
    }

    override fun onDisable() {
        saveConfigurationDefaultPath(globalConfig)
    }

    companion object {
        lateinit var instance: ProjectMCoreMain

        lateinit var socialService: SocialService
        lateinit var globalConfig: GlobalConfig
    }
}