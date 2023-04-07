package firebase

import com.google.firebase.database.*
import model.DeliveryArea
import org.apache.logging.log4j.kotlin.Logging
import java.util.concurrent.Semaphore

class FireBaseRepo(
//    private val dbInstance: FirebaseDatabase,
    private val dbReference: DatabaseReference
) : Logging {

    fun isUserExist(id: String): Boolean {
        var result = false
//        val semaphore = Semaphore(0)
//        val query: Query = dbReference.orderByChild("id").equalTo(id)
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.hasChildren()) {
//                    result = true
//                    semaphore.release()
//                } else {
//                    logger.debug("снапшот пустой, нет такого id")
//                    semaphore.release()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError?) {
//                logger.error(error?.message ?: "Что то полшло не так")
//            }
//        })
//        semaphore.acquire()
        return result
    }

    fun getDeliveryAreas(callback: DeliveryAreaCallback) {

        val result = mutableListOf<DeliveryArea>()

        dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    for (data in snapshot.children)
                        result.add(data.getValue(DeliveryArea::class.java))

                    callback.onDeliveryAreaCallBack(result)

                } else {
                    logger.debug("снапшот пустой")
                    println("снапшот пустой")
                }
            }

            override fun onCancelled(error: DatabaseError?) {
                logger.error(error?.message ?: "Что то полшло не так")
            }
        })
    }

}