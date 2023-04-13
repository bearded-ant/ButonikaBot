package firebase

import com.google.firebase.database.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import model.Courier
import model.DeliveryArea
import model.StartPoint
import org.apache.logging.log4j.kotlin.Logging
import java.util.concurrent.Semaphore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireBaseRepo() : Logging {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private val deliveryAreaRef = firebaseInstance.getReference("deliveryArea")
    private val startPointRef = firebaseInstance.getReference("startPoint")
    private val courierRef = firebaseInstance.getReference("courier")

    fun isUserExist(courierId: String): Boolean {
        var result = false
        val semaphore = Semaphore(0)
        val query = courierRef.orderByChild("id").equalTo(courierId)
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

    suspend fun isRegistrationConfirmed(courierId: String): Boolean {
        delay(5000)
        return true
    }

    suspend fun getDataFromFirebaseRealtimeDatabase(databaseReference: DatabaseReference): DataSnapshot? {
        return suspendCancellableCoroutine { continuation ->
            // Добавляем слушатель ValueEventListener для ожидания загрузки данных
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Вызываем resume, передавая полученный DataSnapshot
                    continuation.resume(dataSnapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Вызываем resumeWithException с ошибкой, если получена ошибка
                    continuation.resumeWithException(error.toException())
                }
            }

            // Добавляем слушатель к базовому DatabaseReference
            databaseReference.addListenerForSingleValueEvent(valueEventListener)

            // Удаляем слушатель при отмене корутины
            continuation.invokeOnCancellation {
                databaseReference.removeEventListener(valueEventListener)
            }
        }
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

    fun getCourierFromId(courierId: String, callback: CourierCallback) {
        var result = Courier()
        val query = courierRef.orderByChild("id").equalTo(courierId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    for (data in snapshot.children) {
                        result = data.getValue(Courier::class.java)
                    }
                    callback.onCourierCallBack(result)

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