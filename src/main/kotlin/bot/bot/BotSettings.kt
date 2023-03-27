package bot.bot

import java.io.IOException
import java.util.*


const val FILE_NAME = "config.properties"

class BotSettings {

    private var properties: Properties? = null
    var token: String? = null
    var userName: String? = null

    init {
        try {
            properties = Properties()
            try {
                val file = this::class.java.classLoader.getResourceAsStream(FILE_NAME)
                properties!!.load(file)

            } catch (e: IOException) {
                throw IOException(String.format("Error loading properties file '%s'", FILE_NAME))
            }
            token = properties!!.getProperty("token")
            if (token == null) {
                throw RuntimeException("Token value is null")
            }
            userName = properties!!.getProperty("username")
            if (userName == null) {
                throw RuntimeException("UserName value is null")
            }
        } catch (e: RuntimeException) {
            throw RuntimeException("Bot initialization error: " + e.message)
        } catch (e: IOException) {
            throw RuntimeException("Bot initialization error: " + e.message)
        }
    }

    companion object {
        var instance: BotSettings? = null
            get() {
                if (field == null) field = BotSettings()
                return field
            }
            private set
    }
}