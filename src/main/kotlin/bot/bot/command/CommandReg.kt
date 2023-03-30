package bot.bot.command

import org.apache.logging.log4j.kotlin.Logging
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

class CommandReg : Command("reg", "регистрация"), Logging {
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {
        message.text = ("Добро пожаловать! \n"
                + "Введите Имя")
        super.processMessage(absSender, message, strings)
    }
}