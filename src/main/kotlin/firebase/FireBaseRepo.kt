package firebase

import com.google.firebase.database.*
import org.apache.logging.log4j.kotlin.Logging

class FireBaseRepo(
//    private val dbInstance: FirebaseDatabase,
    private val dbReference: DatabaseReference
) : Logging {

    fun isUserExist(id: String): Boolean {
        var result = false
        val query: Query = dbReference.orderByChild("id").equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    result = true
                } else logger.debug("снапшот пустой, нет такого id")
            }

            override fun onCancelled(error: DatabaseError?) {
                logger.error(error?.message ?: "Что то полшло не так")
            }
        })
        return result
    }
}