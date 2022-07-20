@file:OptIn(ExperimentalSerializationApi::class)

package pro.darc.projectm.provider.conf.models

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.*
import kotlinx.serialization.ExperimentalSerializationApi
import pro.darc.projectm.provider.conf.Configuration

@Configuration(defaultPath = "global_config.yml")
@kotlinx.serialization.Serializable
data class GlobalConfig(
    @EncodeDefault(ALWAYS)
    var version: Int = 1,
    @EncodeDefault(ALWAYS)
    var debug: Boolean = false
)
