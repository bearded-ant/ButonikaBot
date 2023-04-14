package bot.bot.keyboards

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

class CourierRegKeyboard {

    fun inlineRegistrationKeyboard(): InlineKeyboardMarkup {

        val nameRegistrationButton = "ввести ФИО" to "1"
        val selectPointsButton = "точки старта" to "2"
        val selectAreaButton = "выбрать район доставки" to "3"
        val acceptRegistration = "подтвердить регистрацию" to "4"

        val buttons: List<Pair<String, String>> = listOf(
            nameRegistrationButton,
            selectPointsButton,
            selectAreaButton,
            acceptRegistration
        )

        return InlineButtonFactory.createInlineKeyboardMarkup(buttons)
    }
}