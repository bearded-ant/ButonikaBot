package bot

import org.apache.logging.log4j.kotlin.logger
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

abstract class Command(val commandIdentifier: String, val description: String) : IBotCommand {
    private val log = logger()
    override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>) {
        log.debug("COMMAND: ${message.text} ${strings.toString()}")
        try {
            val sendMessage: SendMessage = SendMessage
                .builder()
                .chatId(message.chatId.toString())
                .text(message.text)
                .build()
            absSender.execute(sendMessage)
        } catch (e: TelegramApiException) {
            log.error("Command message processing error: ${e.message}")
        }
    }
}