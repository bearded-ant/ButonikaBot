import Bot.BotSettings

fun main(args: Array<String>) {

    val srt:BotSettings = BotSettings.instance!!


    println("${srt.token}, ${srt.userName}")
    println("Program arguments: ${args.joinToString()}")
}