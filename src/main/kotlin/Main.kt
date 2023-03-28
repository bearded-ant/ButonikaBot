import bot.bot.BotProcessor
import org.apache.logging.log4j.kotlin.logger

fun main(args: Array<String>) {

    try {
        val botProcessor: BotProcessor = BotProcessor.newInstance()
        logger("main").info("Telegram bot started")
    } catch (e:RuntimeException) {
        logger("main").error(e.message.toString())
    }
}