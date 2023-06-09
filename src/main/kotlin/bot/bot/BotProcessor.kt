package bot.bot

import bot.bot.command.CommandHelp
import bot.bot.command.CommandStart
import bot.bot.keyboards.CourierRegKeyboard
import bot.bot.keyboards.InlineButtonFactory
import firebase.DeliveryAreaCallback
import firebase.FireBaseRepo
import firebase.StartPointCallback
import model.DeliveryArea
import model.StartPoint
import org.apache.logging.log4j.kotlin.logger
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import qr.QRTools
import java.io.File
import java.io.IOException
import java.util.stream.Collectors


class BotProcessor : TelegramLongPollingCommandBot() {
    companion object {
        fun newInstance(): BotProcessor = BotProcessor()

        private const val TEXT_LIMIT = 512
        val botSettings = BotSettings.instance
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        val courierEditFlag = mutableSetOf<String>()
    }

    init {
        try {
            registerBot()
            registerCommands()
        } catch (e: TelegramApiException) {
            logger("botProcessor").error("Telegram Bot initialization error:  ${e.message}")
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
            logger("botProcessor").error(String.format("Sending message error: %s", e.message))
        }
    }

    private fun sendKeyboard(chatId: Long, message: String, keyboard: InlineKeyboardMarkup) {
        try {
            val sendMessage = SendMessage
                .builder()
                .chatId(chatId.toString())
                .replyMarkup(keyboard)
                .text(message)
                .build()
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            logger("botProcessor").error(String.format("Sending keyboard error: %s", e.message))
        }
    }

    private fun sendImage(chatId: Long, path: String) {
        try {
            val photo = SendPhoto()

            photo.photo = InputFile(File(path))
            photo.chatId = chatId.toString()
            execute(photo)

        } catch (e: TelegramApiException) {
            logger("botProcessor").error(String.format("Sending image error: %s", e.message))
        }
    }

    private fun sendQRImage(chatId: Long, path: String) {
        sendImage(chatId, path)
        val file = File(path)
        if (!file.delete()) {
            logger("msg send file").error(String.format("File '%s' removing error", path))
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
//                    MessageType.TEXT -> processTextToQRCode(update)
                }
            } catch (e: UserException) {
                sendMessage(update.message.chatId, e.message!!)
            } catch (e: TelegramApiException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения TE")
                logger("command error").error(String.format("Received message processing error: %s", e.message))
            } catch (e: RuntimeException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения RE")
                logger("command error").error(String.format("Received message processing error: %s", e.message))
            } catch (e: IOException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения IO")
                logger("command error").error(String.format("Received message processing error: %s", e.message))
            } catch (e: UserException) {
                sendMessage(update.message.chatId, "Ошибка обработки сообщения UE")
                logger("command error").error(String.format("Received message processing error: %s", e.message))
            }
        } else if (update.hasCallbackQuery()) {
            val callbackChatId = update.callbackQuery.message.chatId
            when (val callbackData = update.callbackQuery.data) {
                "1" -> {
                    sendMessage(callbackChatId, "введите ФИО")
                    courierEditFlag.add(callbackChatId.toString())
                }

                "2" -> FireBaseRepo().getStartPoint(
                    object : StartPointCallback {
                        override fun onStartPointCallBack(startPoints: List<StartPoint>) {
                            val msgString = StringBuilder("Выберите одну точку старта")
                            val buttons = mutableListOf<Pair<String, String>>()
                            for (data in startPoints)
                                buttons.add(data.name to data.id.toString())

                            sendKeyboard(
                                callbackChatId,
                                msgString.toString(),
                                InlineButtonFactory.createInlineKeyboardMarkup(buttons)
                            )
                        }
                    })

                "3" -> FireBaseRepo().getDeliveryAreas(
                    object : DeliveryAreaCallback {
                        override fun onDeliveryAreaCallBack(deliveryAreas: List<DeliveryArea>) {
                            val msgString = StringBuilder("Выберите один район доставки")
                            val buttons = mutableListOf<Pair<String, String>>()
                            for (data in deliveryAreas)
                                buttons.add(data.name to data.id.toString())

                            sendKeyboard(
                                callbackChatId,
                                msgString.toString(),
                                InlineButtonFactory.createInlineKeyboardMarkup(buttons)
                            )
                        }
                    })

                "4" -> {
                    FireBaseRepo().updateCourierRegStatus(callbackChatId.toString(), true)
                    sendMessage(callbackChatId, "отправил запрос регистрации менеджеру")
                }

                "11", "12" -> {
                    FireBaseRepo().updateCourierStartPoint(callbackChatId.toString(), update.callbackQuery.data.toInt())
                    sendMessage(callbackChatId, "записал \n")
                    sendKeyboard(callbackChatId, "продолжим", CourierRegKeyboard().inlineRegistrationKeyboard())
                }

                "21", "22", "23", "24" -> {
                    FireBaseRepo().updateCourierDeliveryArea(
                        callbackChatId.toString(),
                        update.callbackQuery.data.toInt()
                    )
                    sendMessage(callbackChatId, "записал \n")
                    sendKeyboard(callbackChatId, "продолжим", CourierRegKeyboard().inlineRegistrationKeyboard())
                }
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
            logger("message type").error(String.format("Invalid message type: %s", e.message))
            null
        }
    }

    private fun processImage(update: Update) {
        logMessage(update.message.chatId, update.message.from.id, true, "\$image")

        val fileUrl = getFileUrl(update)
        val text: String = QRTools().getTextFromQR(fileUrl)

        logMessage(update.message.chatId, update.message.from.id, false, text)
        sendMessage(update.message.chatId, text)
    }

    private fun processText(update: Update) {
        val id = update.message.chatId.toString()
        val name = update.message.text

        if (id in courierEditFlag) {
            if (checkInputText(name)) {
                courierEditFlag.remove(id)
                FireBaseRepo().updateCourierName(id, name)
                sendMessage(id.toLong(), "Записал [ $name ] \n")
                sendKeyboard(id.toLong(), "продолжим", CourierRegKeyboard().inlineRegistrationKeyboard())
            } else {
                sendMessage(id.toLong(), "Допустимы только русские и английские буквы. попробуйте еще раз")
            }
        } else {
            sendMessage(id.toLong(), "Не знаю что делать")
        }

    }

    private fun checkInputText(inputString: String): Boolean {
        val regex = Regex("""^[а-яА-Яa-zA-Z\s]+$""")
        return inputString.matches(regex)
    }

    private fun processTextToQRCode(update: Update) {
        val text = update.message.text

        logMessage(update.message.chatId, update.message.from.id, true, text)

        if (text.length > TEXT_LIMIT) {
            logger("msg text").info(String.format("Message exceeds maximum length of %d", TEXT_LIMIT))
        }

        val imageUrl: String = QRTools().encodeText(text)

        logMessage(update.message.chatId, update.message.from.id, false, "\$image")
        sendQRImage(update.message.chatId, imageUrl)
    }


    private fun getFileUrl(update: Update): String {
        val photoSizes: List<PhotoSize> = update.message.photo
        val getFile = GetFile(update.message.photo[photoSizes.lastIndex].fileId)
        return execute(getFile).getFileUrl(botToken)
    }


    private fun logMessage(chatId: Long, userId: Long, input: Boolean, logText: String) {
        var text = logText
        if (text.length > TEXT_LIMIT) text = text.substring(0, TEXT_LIMIT)
        logger("main log").info(
            String.format("CHAT [%d] MESSAGE %s %d: %s", chatId, if (input) "FROM" else "TO", userId, text)
        )
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
//        register(CommandReg())
        setRegisteredCommands()
    }

    private fun registerBot() {
        try {
            telegramBotsApi.registerBot(this)
        } catch (e: TelegramApiException) {
            logger("telegram").error("Telegram API initialization error: + ${e.message}")
            throw RuntimeException("Telegram API initialization error: " + e.message)
        }
    }
}