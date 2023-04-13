import bot.bot.BotProcessor
import firebase.FirebaseInit
import org.apache.logging.log4j.kotlin.logger

fun main(args: Array<String>) {

    try {
        BotProcessor.newInstance()
        logger("main").info("Telegram bot started")
    } catch (e: RuntimeException) {
        logger("main").error(e.message.toString())
    }
    val fireBirdInit = FirebaseInit()
}