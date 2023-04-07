package model

data class DeliveryArea(
    val id: Int = 0,
    val name: String = "",
    val totalNeedCourier: Int = 0,
    val registeredCouriers: Int = 0
)