package pro.darc.projectm.services.social

import pro.darc.projectm.dsl.Log
import pro.darc.projectm.provider.conf.WithConfiguration
import pro.darc.projectm.provider.conf.getDefaultConfiguration
import pro.darc.projectm.provider.conf.models.SocialConfig
import pro.darc.projectm.services.CoreService

class SocialService: CoreService(), WithConfiguration {

    lateinit var socialConfig: SocialConfig

    init {
    }

    override fun onLoad() {
        Log.info("load")
        socialConfig = getDefaultConfiguration()
    }

    override fun onStart() {
    }

    override fun onStop() {
        Log.info("stop")
    }
}