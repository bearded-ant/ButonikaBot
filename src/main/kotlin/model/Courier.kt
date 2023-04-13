package model

data class Courier(
    val id: String = "",
    val name: String = "",
    val nicName: String = "",
    val deliveryAreaId: Int = 0,
    val startPointId:Int = 0,
    val isRegistrationConfirmed:Boolean = false
)