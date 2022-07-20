package pro.darc.projectm.provider.conf

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.encodeToString
import pro.darc.projectm.ProjectMCoreMain
import java.io.File

interface WithConfiguration

inline fun<reified C> WithConfiguration.getConfiguration(path: String): C {
    val file = File(ProjectMCoreMain.instance.dataFolder, path)

    if (!file.exists()) {
        val factory = C::class.java.getConstructor()
        val data = factory.newInstance()
        saveConfiugration(data, path)
    }

    val texts = file.inputStream()
    return Yaml.default.decodeFromStream(texts)
}

inline fun<reified C> WithConfiguration.getConfiguration(block: ()->String): C = getConfiguration(block())

inline fun<reified C> WithConfiguration.getDefaultConfiguration(): C = getConfiguration { getDefaultPath<C>() }

inline fun<reified C> getDefaultPath(): String {
    val configAnno = C::class.annotations.firstOrNull { it.annotationClass == Configuration::class }
    return if (configAnno != null) {
        (configAnno as Configuration).defaultPath
    } else {
        "${C::class.simpleName ?: C::class.java.typeName}.yml"
    }
}

inline fun<reified C> WithConfiguration.saveConfiugration(data: C, path: String) {
    File(ProjectMCoreMain.instance.dataFolder, path).apply {
        if (!exists()) createNewFile()
        writeText(Yaml.default.encodeToString(data))
    }
}

inline fun<reified C> WithConfiguration.saveConfigurationDefaultPath(data: C) = saveConfiugration(data, getDefaultPath<C>())

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Configuration(val defaultPath: String)
