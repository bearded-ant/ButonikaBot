package bot.bot.command

import bot.bot.keyboards.CourierRegKeyboard
import firebase.FireBaseRepo
import model.Courier
import org.apache.logging.log4j.kotlin.Logging
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

class CommandReg : Command("reg", "регистрация"), Logging {
    private val courierRegKeyboard: CourierRegKeyboard = CourierRegKeyboard()
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {

        val fireBaseRepo = FireBaseRepo()

        if (!fireBaseRepo.isUserExist(message.chatId.toString())) {
            val courierNicName = message.from.userName ?: "anonymous"
            val courierRealName = "name"
            val courierId = message.chatId.toString()

            val courier = Courier(
                id = courierId,
                name = courierRealName,
                nicName = courierNicName)

            fireBaseRepo.putCourier(courier)

            message.text = ("Добро пожаловать, $courierNicName!")
            message.replyMarkup = courierRegKeyboard.inlineRegistrationKeyboard()

        } else message.text = ("Вы уже зарегистрированы")
        super.processMessage(absSender, message, strings)
    }
}