@file:OptIn(ExperimentalSerializationApi::class)

package pro.darc.projectm.provider.conf.models

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import pro.darc.projectm.provider.conf.Configuration

@Configuration(defaultPath = "social.yml")
@kotlinx.serialization.Serializable
data class SocialConfig(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    var version: Int = 1,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    var showJoinMessage: Boolean = false,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    var showLeaveMessage: Boolean = false,
)
