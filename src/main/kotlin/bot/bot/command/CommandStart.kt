package bot.bot.command

import org.apache.logging.log4j.kotlin.Logging
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

class CommandStart : Command("start", "Запуск бота"), Logging {
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {
        message.text = ("Добро пожаловать! \n"
                + "Вас приветствует бот @ButonikaSamaraBot, у меня простые функции: чтение и генерация QR-кодов. \n"
                + "Начнём?")
        super.processMessage(absSender, message, strings)
    }
}