package firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

class FirebaseInit {
    init {

        val settings: FireBaseSettings = FireBaseSettings.instance!!

        val serviceAccount: FileInputStream = FileInputStream("${settings.fireBaseTokenName}")

        val fireBaseOption: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("${settings.fireBaseUrl}")
            .build()

        FirebaseApp.initializeApp(fireBaseOption)

//        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("")
//
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onCancelled(error: DatabaseError?) {
//                TODO("Not yet implemented")
//            }
//        })
    }
}