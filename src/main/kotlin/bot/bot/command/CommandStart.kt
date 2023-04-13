package bot.bot.command

import bot.bot.keyboards.CourierRegKeyboard
import firebase.FireBaseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import model.Courier
import org.apache.logging.log4j.kotlin.Logging
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import sun.rmi.server.Dispatcher
import kotlin.coroutines.suspendCoroutine

class CommandStart : Command("start", "Запуск бота"), Logging {
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {
        val courierRegKeyboard: CourierRegKeyboard = CourierRegKeyboard()
        val fireBaseRepo = FireBaseRepo()

        val scope = CoroutineScope(Dispatchers.Default)
        val isReg: Boolean = scope.launch { fireBaseRepo.isRegistrationConfirmed(message.chatId.toString()) }.start()
        if (fireBaseRepo.isUserExist(message.chatId.toString())) {
            if (isReg) {
//                //todo sey hello
                message.text = ("Вы уже зарегистрированы")
                super.processMessage(absSender, message, strings)
            } else {
//                //todo say await
                message.text = ("Ждите подтверждения регистрации")
                super.processMessage(absSender, message, strings)
            }
        } else {
            //todo create Courier, send keyboard
            fireBaseRepo.putCourier(createMinimalCourier(message))
            message.text = ("Добро пожаловать, завершите регистрацию")
            message.replyMarkup = courierRegKeyboard.inlineRegistrationKeyboard()
            super.processMessage(absSender, message, strings)
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