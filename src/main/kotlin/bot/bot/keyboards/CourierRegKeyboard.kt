package bot.bot.keyboards

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

class CourierRegKeyboard {

    fun inlineRegistrationKeyboard(): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()

        val nameRegistrationButton = InlineKeyboardButton()
        val selectPointsButton = InlineKeyboardButton()
        val selectAreaButton = InlineKeyboardButton()

        val acceptRegistration = InlineKeyboardButton()

        nameRegistrationButton.text = "ввести ФИО"
        nameRegistrationButton.callbackData = "1"

        selectPointsButton.text = "точки старта"
        selectPointsButton.callbackData = "2"

        selectAreaButton.text = "выбрать район доставки"
        selectAreaButton.callbackData = "3"

        acceptRegistration.text = "подтвердить регистрацию"
        acceptRegistration.callbackData = "4"

        val regRow: MutableList<InlineKeyboardButton> = ArrayList()
        val acceptRow: MutableList<InlineKeyboardButton> = ArrayList()

        regRow.add(nameRegistrationButton)
        regRow.add(selectPointsButton)
        regRow.add(selectAreaButton)

        acceptRow.add(acceptRegistration)

        val courierRegistration: MutableList<List<InlineKeyboardButton>> = ArrayList()
        courierRegistration.add(regRow)
        courierRegistration.add(acceptRow)

        inlineKeyboardMarkup.keyboard = courierRegistration

        return inlineKeyboardMarkup
    }
}