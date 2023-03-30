package bot.bot.command

import com.google.firebase.database.FirebaseDatabase
import firebase.FireBaseRepo
import model.Courier
import org.apache.logging.log4j.kotlin.Logging
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

class CommandReg : Command("reg", "регистрация"), Logging {
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val courierRef = database.getReference("courier")
        val fireBaseRepo = FireBaseRepo(courierRef)

        if (!fireBaseRepo.isUserExist(message.chatId.toString())) {
            val courierNicName = message.from.userName ?: "anonymous"
            val courierRealName = "name"
            val courierId = message.chatId.toString()

            val courier: Courier = Courier(
                id = courierId,
                name = courierRealName,
                nicName = courierNicName,
                deliveryArea = 1
            )
            val courierMap = mutableMapOf<String, Courier>()
            courierMap[message.chatId.toString()] = courier

            courierRef.setValueAsync(courierMap)
            message.text = ("Добро пожаловать, $courierNicName!")
        }
        else message.text = ("Вы уже зарегистрированы")
        super.processMessage(absSender, message, strings)
    }
}