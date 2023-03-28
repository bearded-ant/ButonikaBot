package bot.bot

import bot.bot.command.CommandHelp
import bot.bot.command.CommandStart
import bot.bot.qr.QRTools
import org.apache.logging.log4j.kotlin.logger
import org.json.JSONObject
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.io.File
import java.io.IOException
import java.util.stream.Collectors


class BotProcessor : TelegramLongPollingCommandBot() {

    companion object {
        fun newInstance(): BotProcessor = BotProcessor()

        private const val TEXT_LIMIT = 512
        val botSettings = BotSettings.instance
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
    }

    init {
        try {
            registerBot()
            registerCommands()
        } catch (e: TelegramApiException) {
            throw RuntimeException("Telegram Bot initialization error: " + e.message)
        }
    }

    private fun sendMessage(chatId: Long, message: String) {
        try {
            val sendMessage = SendMessage
                .builder()
                .chatId(chatId.toString())
                .text(message)
                .build()
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            logger(String.format("Sending message error: %s", e.message))
        }
    }

    private fun sendImage(chatId: Long, path: String) {
        try {
            val photo = SendPhoto()

            photo.photo = InputFile(File(path))
            photo.chatId = chatId.toString()
            execute(photo)

        } catch (e: TelegramApiException) {
            println(String.format("Sending image error: %s", e.message))
        }
    }

    private fun sendQRImage(chatId: Long, path: String) {
        sendImage(chatId, path)
        val file = File(path)
        if (!file.delete()) {
            println(String.format("File '%s' removing error", path))
        }
    }

    override fun getBotToken(): String = botSettings?.token!!
    override fun getBotUsername(): String = botSettings?.userName!!
    override fun processInvalidCommandUpdate(update: Update) {
        val command = update.message.text.substring(1)
        val registeredCommandIdentifier = mutableListOf<String>()
        registeredCommands.forEach { registeredCommandIdentifier.add("/${it.commandIdentifier}") }

        sendMessage(
            update.message.chatId,
            "Некорректная команда [$command], доступные команды: \n $registeredCommandIdentifier"
        )
    }

    override fun processNonCommandUpdate(update: Update) {
        if (update.hasMessage()) {
            try {
                when (getMessageType(update)!!) {
                    MessageType.COMMAND -> processInvalidCommandUpdate(update)
                    MessageType.IMAGE -> processImage(update)
                    MessageType.TEXT -> processText(update)
                }
            } catch (e: UserException) {
                sendMessage(update.message.chatId, e.message!!)
            } catch (e: TelegramApiException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения TE")
                println(String.format("Received message processing error: %s", e.message))
            } catch (e: RuntimeException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения RE")
                println(String.format("Received message processing error: %s", e.message))
            } catch (e: IOException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения IO")
                println(String.format("Received message processing error: %s", e.message))
            } catch (e: UserException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения UE")
                println(String.format("Received message processing error: %s", e.message))
            }
        }
    }


    private fun getMessageType(update: Update): MessageType? {
        var messageType: MessageType? = null
        return try {
            messageType = if (update.message.photo != null)
                MessageType.IMAGE
            else if (update.message.text != null && (update.message.text.matches("""^/\w*$""".toRegex())))
                MessageType.COMMAND
            else MessageType.TEXT

            messageType
        } catch (e: RuntimeException) {
            logger(String.format("Invalid message type: %s", e.message))
            messageType
        }
    }

    private fun processImage(update: Update) {
        logMessage(update.message.chatId, update.message.from.id, true, "\$image")

        val photoSizes: List<PhotoSize> = update.message.photo
        val fileUrl: String = getFileUrl(update.message.photo[photoSizes.lastIndex].fileId)

        val text: String = QRTools().getTextFromQR(fileUrl)

        logMessage(update.message.chatId, update.message.from.id, false, text)
        sendMessage(update.message.chatId, text)
    }

    private fun processText(update: Update) {
        val text = update.message.text
        logMessage(
            update.message.chatId,
            update.message.from.id,
            true,
            text
        )
        if (text.length > TEXT_LIMIT) {
            logger(String.format("Message exceeds maximum length of %d", TEXT_LIMIT))
        }

        val imageUrl: String = QRTools().encodeText(text)

        logMessage(update.message.chatId, update.message.from.id, false, "\$image")
        sendQRImage(update.message.chatId, imageUrl)
    }


    private fun getFileUrl(fileId: String): String {
        val jsonObject: JSONObject = getFileRequest(fileId)
        return ("https://api.telegram.org/file/bot%s/%s${botSettings!!.token}${jsonObject.get("file_path")}")
    }

    private fun getFileRequest(fileId: String): JSONObject {
        val fileUrl = String.format(
            "https://api.telegram.org/bot%s/getFile?file_id=%s",
            botSettings!!.token,
            fileId
        )
        return IOTools.readJsonFromUrl(fileUrl)
    }

    private fun logMessage(chatId: Long, userId: Long, input: Boolean, logText: String) {
        var text = logText
        if (text.length > TEXT_LIMIT) text = text.substring(0, TEXT_LIMIT)
        logger().info(String.format("CHAT [%d] MESSAGE %s %d: %s", chatId, if (input) "FROM" else "TO", userId, text))
    }

    private fun setRegisteredCommands() {
        val regCommands = registeredCommands
        regCommands
            .stream()
            .map(IBotCommand::getCommandIdentifier)
            .collect(Collectors.toList())
    }

    private fun registerCommands() {
        register(CommandStart())
        register(CommandHelp())
        setRegisteredCommands()
    }

    private fun registerBot() {
        try {
            telegramBotsApi.registerBot(this)
        } catch (e: TelegramApiException) {
            throw RuntimeException("Telegram API initialization error: " + e.message)
        }
    }
}