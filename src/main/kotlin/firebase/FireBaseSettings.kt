package firebase

import java.io.IOException
import java.util.*


const val FILE_NAME = "config.properties"

class FireBaseSettings {

    private var properties: Properties? = null
    var fireBaseUrl: String? = null
    var fireBaseTokenName: String? = null

    init {
        try {
            properties = Properties()
            try {
                val file = this::class.java.classLoader.getResourceAsStream(FILE_NAME)
                properties!!.load(file)

            } catch (e: IOException) {
                throw IOException(String.format("Error loading properties file '%s'", FILE_NAME))
            }
            fireBaseUrl = properties!!.getProperty("fireBaseUrl")
            if (fireBaseUrl == null) {
                throw RuntimeException("FireBaseUrl value is null")
            }
            fireBaseTokenName = properties!!.getProperty("fireBaseTokenName")
            if (fireBaseTokenName == null) {
                throw RuntimeException("FireBase Token name value is null")
            }
        } catch (e: RuntimeException) {
            throw RuntimeException("Firebase initialization error: " + e.message)
        } catch (e: IOException) {
            throw RuntimeException("Firebase initialization error: " + e.message)
        }
    }

    companion object {
        var instance: FireBaseSettings? = null
            get() {
                if (field == null) field = FireBaseSettings()
                return field
            }
            private set
    }
}