package pro.darc.projectm.services.persistence

import org.apache.commons.dbcp2.BasicDataSource
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import pro.darc.projectm.ProjectMCoreMain

object SqliteAdapter: Adapter {

    private var db: Database = Database.connect("sqlite:///${ProjectMCoreMain.instance.dataFolder}/localstorage.sqlite3")

    override fun getKeys() {
        TODO("Not yet implemented")
    }

    override fun <T> get(key: String): T {
        TODO("Not yet implemented")
    }

    override fun <T> set(key: String, value: T) {
        TODO("Not yet implemented")
    }
}

interface KeyValuePair: Entity<KeyValuePair> {
    companion object : Entity.Factory<KeyValuePair>()
    val id: Int
    val key: String
    val value: String
}

object KeyValuePairs: Table<KeyValuePair>("t_key_value_storage") {
    val id = int("id").primaryKey().bindTo { it.id }
    val key = varchar("key").bindTo { it.key }
    val value = text("value").bindTo { it.value }
}
