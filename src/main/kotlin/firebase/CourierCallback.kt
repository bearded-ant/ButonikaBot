package firebase

import model.Courier

interface CourierCallback {
    fun onCourierCallBack(data: Courier)
}