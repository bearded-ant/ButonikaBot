package bot.bot.keyboards

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

object InlineButtonFactory {
    fun createInlineKeyboardMarkup(vararg buttons: Pair<String, String>): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val inlineKeyboardButtons = mutableListOf<List<InlineKeyboardButton>>()

        for (button in buttons) {
            val inlineKeyboardButton = InlineKeyboardButton()
            inlineKeyboardButton.text = button.first
            inlineKeyboardButton.callbackData = button.second
            inlineKeyboardButtons.add(listOf(inlineKeyboardButton))
        }

        inlineKeyboardMarkup.keyboard = inlineKeyboardButtons
        return inlineKeyboardMarkup
    }
}