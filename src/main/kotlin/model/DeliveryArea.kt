package model

data class DeliveryArea(
    val id:Int,
    val name:String,
    val totalNeedCourier: Int,
    val registeredCouriers:Int
)