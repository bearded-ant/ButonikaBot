package bot.bot.command

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender


class CommandHelp: Command("help","Справка") {
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {
        message.text = "Функции чат-бота: \n" +
                "- считывание QR-кода: для считывания QR-кода сфотографируйте код и отправьте изображение в чат \n" +
                "- генерация QR-кода: для генерации QR-кода отправьте текст или ссылку в чат"
        super.processMessage(absSender, message, strings)
    }
}

