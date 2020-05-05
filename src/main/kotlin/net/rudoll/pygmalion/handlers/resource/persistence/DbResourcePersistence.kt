package net.rudoll.pygmalion.handlers.resource.persistence

import com.couchbase.lite.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken


class DbResourcePersistence(private val resourcePath: String) : ResourcePersistence {
    private val database: Database
    private val gson = Gson()
    private val jsonParser = JsonParser()
    val dbPath: String = System.getProperty("java.io.tmpdir") + "/pygmalion/default.db"

    init {
        val config = DatabaseConfiguration()
        config.directory = dbPath
        database = Database(DATABASE_NAME, config)
    }

    override val values: List<JsonElement>
        get() {
            val query: Query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(database))
                    .where(Meta.id.like(Expression.string("$resourcePath%")))
            val result: ResultSet = query.execute()
            return result.allResults().map { it.toMap()[DATABASE_NAME].toJsonElement() }
        }

    override fun get(key: String): JsonElement? {
        return database.getDocument(resourcePath + key)?.toMap()?.toJsonElement()
    }

    override fun set(key: String, value: JsonElement) {
        val map: Map<String, Any> = gson.fromJson(gson.toJson(value), object : TypeToken<Map<String, Any>>() {}.type) //not exactly pretty or performant
        val doc = MutableDocument(resourcePath + key, map)
        database.save(doc)
    }

    override fun remove(id: String) {
        database.delete(database.getDocument(resourcePath + id))
    }

    private fun Any?.toJsonElement(): JsonElement = jsonParser.parse(gson.toJson(this))

    companion object {
        private const val DATABASE_NAME = "PYGMALION_DB"

        init {
            CouchbaseLite.init()
        }
    }
}

