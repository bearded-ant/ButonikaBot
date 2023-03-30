package firebase

import com.google.firebase.database.*
import org.apache.logging.log4j.kotlin.Logging
import java.util.concurrent.Semaphore

class FireBaseRepo(
//    private val dbInstance: FirebaseDatabase,
    private val dbReference: DatabaseReference
) : Logging {

    fun isUserExist(id: String): Boolean {
        val semaphore = Semaphore(0)
        var result = false
        val query: Query = dbReference.orderByChild("id").equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    result = true
                    semaphore.release()
                } else {
                    logger.debug("снапшот пустой, нет такого id")
                    semaphore.release()
                }
            }

            override fun onCancelled(error: DatabaseError?) {
                logger.error(error?.message ?: "Что то полшло не так")
            }
        })
        semaphore.acquire()
        return result
    }
}