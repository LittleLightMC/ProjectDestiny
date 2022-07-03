package pro.darc.projectm.services.social

import pro.darc.projectm.dsl.Log
import pro.darc.projectm.dsl.ProjectMListener

class DisplayName : ProjectMListener() {
    init {
        Log.info("DisplayName Module started")
        server.pluginManager.registerEvents(this, this)
    }
}