package bot

import jdk.jshell.spi.ExecutionControl.UserException
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


class BotProcessor : TelegramLongPollingCommandBot() {
    private val botSettings = BotSettings.instance

    fun sendMessage(chatId: Long, message: String?) {
        try {
            val sendMessage = SendMessage
                .builder()
                .chatId(chatId.toString())
                .text(message!!)
                .build()
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            println(String.format("Sending message error: %s", e.message))
        }
    }


    fun sendImage(chatId: Long, path: String) {
        try {
            val photo = SendPhoto()
            photo.photo = InputFile(path)
            photo.chatId = chatId.toString()
            execute(photo)
        } catch (e: TelegramApiException) {
            println(String.format("Sending image error: %s", e.message))
        }
    }

    override fun getBotUsername(): String = botSettings?.userName!!
    override fun processNonCommandUpdate(update: Update?) {
        TODO("Not yet implemented")
    }

}