package bot.bot.command

import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

abstract class Command(
    private val commandIdentifier: String,
    private val description: String) : IBotCommand, Logging {
    override fun getCommandIdentifier(): String = commandIdentifier

    override fun getDescription(): String = description

    open override fun processMessage(absSender: AbsSender, message: Message, strings: Array<String>?) {
        logger.debug(String.format("COMMAND: %s(%s)", message.text, strings.toString()))
        try {
            val sendMessage = SendMessage
                .builder()
                .chatId(message.chatId.toString())
                .text(message.text)
                .build()
            absSender.execute(sendMessage)
            logger.info("${message.text} // ${sendMessage.text}")
        } catch (e: TelegramApiException) {
            logger.error(String.format("Command message processing error: %s", e.message, e))
        }
    }
}