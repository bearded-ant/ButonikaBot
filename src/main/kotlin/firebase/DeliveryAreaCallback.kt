package firebase

import model.DeliveryArea

interface DeliveryAreaCallback {
    fun onDeliveryAreaCallBack(data: List<DeliveryArea>)
}