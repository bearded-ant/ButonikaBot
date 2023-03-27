package bot.bot.command

import org.apache.logging.log4j.kotlin.Logging
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class CommandStart : IBotCommand, Logging {


    //
//    fun processMessage(absSender: AbsSender, message: Message) {
//        message.text = ("Добро пожаловать! \n"
//                + "Вас приветствует бот @QRVisorBot, у меня простые функции: чтение и генерация QR-кодов. \n"
//                + "Начнём?")
//        super.processMessage(absSender, message, null)
//    }
    override fun getCommandIdentifier(): String  = "start"

    override fun getDescription(): String = "Запуск бота"

    override fun processMessage(absSender: AbsSender?, message: Message?, arguments: Array<out String>?) {
        logger.debug(String.format("COMMAND: %s(%s)", message!!.text, arguments.toString()))
        try {
            val sendMessage = SendMessage
                .builder()
                .chatId(message.chatId.toString())
                .text(message.text)
                .build()
            absSender!!.execute(sendMessage)
        } catch (e: TelegramApiException) {
            logger.error(String.format("Command message processing error: %s", e.message, e))
        }
    }

}