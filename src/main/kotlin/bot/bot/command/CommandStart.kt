package bot.bot.command

import bot.bot.keyboards.CourierRegKeyboard
import firebase.FireBaseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Courier
import org.apache.logging.log4j.kotlin.Logging
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

class CommandStart : Command("start", "Запуск бота"), Logging {
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {
        val courierRegKeyboard = CourierRegKeyboard()
        val fireBaseRepo = FireBaseRepo()

        CoroutineScope(Dispatchers.Default).launch {
            if (fireBaseRepo.isUserExist(message.chatId.toString())) {
                val registrationConfirmed = fireBaseRepo.isRegistrationConfirmed(message.chatId.toString())
                if (registrationConfirmed) {
                    message.text = ("Вы уже зарегистрированы")
                    super.processMessage(absSender, message, strings)
                } else {
                    message.text = ("Ждите подтверждения регистрации")
                    super.processMessage(absSender, message, strings)
                }
            } else {
                fireBaseRepo.putCourier(createMinimalCourier(message))
                message.text = ("Добро пожаловать, завершите регистрацию")
                message.replyMarkup = courierRegKeyboard.inlineRegistrationKeyboard()
                super.processMessage(absSender, message, strings)
            }
        }
    }

    private fun createMinimalCourier(message: Message): Courier {
        val courierNicName = message.from.userName ?: "anonymous"
        val courierRealName = "name"
        val courierId = message.chatId.toString()

        return Courier(
            id = courierId,
            name = courierRealName,
            nicName = courierNicName
        )
    }
}