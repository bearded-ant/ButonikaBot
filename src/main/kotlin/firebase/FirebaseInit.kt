package firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

class FirebaseInit {
    init {

        val settings: FireBaseSettings = FireBaseSettings.instance!!

        val serviceAccount = FileInputStream("${settings.fireBaseTokenName}")

        val fireBaseOption: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("${settings.fireBaseUrl}")
            .build()

        FirebaseApp.initializeApp(fireBaseOption)
    }
}