package firebase

import model.DeliveryArea

interface DeliveryAreaCallback {
    fun onDeliveryAreaCallBack(deliveryAreas: List<DeliveryArea>)
}