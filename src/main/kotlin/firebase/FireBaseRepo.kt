package firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import model.Courier
import model.DeliveryArea
import model.StartPoint
import org.apache.logging.log4j.kotlin.Logging

class FireBaseRepo() : Logging {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private val deliveryAreaRef = firebaseInstance.getReference("deliveryArea")
    private val startPointRef = firebaseInstance.getReference("startPoint")
    private val courierRef = firebaseInstance.getReference("courier")

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

        deliveryAreaRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

    fun getStartPoint(callback: StartPointCallback) {
        val result = mutableListOf<StartPoint>()
        startPointRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    for (data in snapshot.children)
                        result.add(data.getValue(StartPoint::class.java))

                    callback.onStartPointCallBack(result)

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

    fun updateCourierDeliveryArea(courierId: String, deliveryAreaId: Int) {
        val dbUpdateFormat = mutableMapOf<String, Int>()
        dbUpdateFormat.put("$courierId/deliveryAreaId", deliveryAreaId)
        courierRef.updateChildrenAsync(dbUpdateFormat as Map<String, Any>)
    }

    fun updateCourierStartPoint(courierId: String, startPointId: Int) {
        val dbUpdateFormat = mutableMapOf<String, Int>()
        dbUpdateFormat.put("$courierId/startPoint", startPointId)
        courierRef.updateChildrenAsync(dbUpdateFormat as Map<String, Any>)
    }

    fun updateCourierName(courierId: String, name: String) {
        val dbUpdateFormat = mutableMapOf<String, String>()
        dbUpdateFormat.put("$courierId/name", name)
        courierRef.updateChildrenAsync(dbUpdateFormat as Map<String, Any>)
    }

    fun putCourier(courier: Courier) {
        val dbPutFormat = mutableMapOf<String, Courier>()
        dbPutFormat.put(courier.id, courier)
        courierRef.setValueAsync(dbPutFormat)
    }
}