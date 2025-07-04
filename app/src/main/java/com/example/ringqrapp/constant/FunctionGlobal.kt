package com.example.ringqrapp.constant

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.ringqrapp.R
import com.example.ringqrapp.model.BloodOxygen
import com.example.ringqrapp.model.Faq
import com.example.ringqrapp.model.FeatureOption
import com.example.ringqrapp.model.HealthItem
import com.example.ringqrapp.model.HeartRate
import com.example.ringqrapp.model.OptionType
import com.example.ringqrapp.model.Praise
import com.example.ringqrapp.model.Sleep
import com.example.ringqrapp.model.Stress
import com.example.ringqrapp.model.TemperatureBody
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.Normalizer
import java.util.Base64
import java.util.regex.Pattern

object FunctionGlobal {
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isEmailValid2(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$"
        return email.matches(emailRegex.toRegex())
    }

    fun isPasswordValid(password: String): Boolean {
        val passwordRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val phoneNumberRegex = "^(03|05|07|08)\\d{8}$".toRegex()
        return phoneNumber.matches(phoneNumberRegex)
    }

    fun validateUsername(username: String): Boolean {
        val usernameRegex = "^[\\w\\s]{6,20}$".toRegex()
        return username.matches(usernameRegex)
    }

    fun getTextSearch(input: String?): String {
        val nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCOMBINING_DIACRITICAL_MARKS}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }

    fun hideSoftKeyboard(activity: Activity?) {
        try {
            val inputMethodManager =
                activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPasswordWithSalt(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val saltedPassword = password + salt
        val hashedBytes = digest.digest(saltedPassword.toByteArray())
        return hashedBytes.joinToString("") { String.format("%02x", it) }
    }

    fun listFeatureOptions(context: Context): List<FeatureOption> {
        return listOf(
            FeatureOption(
                iconRes = R.drawable.ic_search,
                title = context.resources.getString(R.string.find_device),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_blue_light
                )!!,
                type = OptionType.NAVIGATION,
                route = "find_device"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_theme,
                title = context.resources.getString(R.string.theme_interface),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle
                )!!,
                type = OptionType.NAVIGATION,
                route = "theme_interface"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_ruler,
                title = context.resources.getString(R.string.unit_system_metric),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_green_light
                )!!,
                type = OptionType.VALUE,
                value = context.resources.getString(R.string.value_length_unit_m),
                route = "system_metric"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_thermometer,
                title = context.resources.getString(R.string.temperature_unit),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_red_light
                )!!,
                type = OptionType.VALUE,
                value =  context.resources.getString(R.string.type_measure_temperature_c),
                route = "temperature_unit"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_low_battery,
                title = context.resources.getString(R.string.low_battery_reminder),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_blue_light
                )!!,
                type = OptionType.SWITCH,
                route = "low_battery"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_heart,
                title = context.resources.getString(R.string.healthy_connection),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_blue_light
                )!!,
                type = OptionType.NAVIGATION,
                route = "healthy_connection"
            ), FeatureOption(
                iconRes = R.drawable.ic_file,
                title = context.resources.getString(R.string.background_operation_policy),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_green_light
                )!!,
                type = OptionType.NAVIGATION,
                route = "background_operation_policy"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_up,
                title = context.resources.getString(R.string.firmware_upgrade),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_blue_light
                )!!,
                type = OptionType.NAVIGATION,
                route = "firmware_upgrade"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_settings,
                title = context.resources.getString(R.string.system_settings),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_grey_light
                )!!,
                type = OptionType.NAVIGATION,
                route = "system_settings"
            ),
            FeatureOption(
                iconRes = R.drawable.ic_question,
                title = context.resources.getString(R.string.faq),
                idBackgroundIcon = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_background_circle_grey_light
                )!!,
                type = OptionType.NAVIGATION,
                route = "faq"
            )
        )
    }

    fun listHealth(context: Context): List<HealthItem> {
        return listOf(
            HealthItem.Praise(
                praise = Praise(
                    icon = R.drawable.icc_healing,
                    name = context.resources.getString(R.string.text_categories_congtaguration),
                    message = context.resources.getString(R.string.text_content_categories_congtaguration),
                    background = R.drawable.img_congratulation,
                    timestamp = System.currentTimeMillis()
                )
            ),
            HealthItem.Sleep(
                sleep = Sleep(
                    icon = R.drawable.sleep_123,
                    name = context.resources.getString(R.string.text_categories_sleep),
                    message = context.resources.getString(R.string.text_content_categories_sleep),
                    background = R.drawable.img_sleep,
                    timeSleep = 12,
                    timestamp = System.currentTimeMillis()
                )
            ),
            HealthItem.HeartRate(
                heart_rate = HeartRate(
                    icon = R.drawable.heartbeat,
                    name = context.resources.getString(R.string.text_categories_heart_rate),
                    message = context.resources.getString(R.string.text_content_categories_heart_rate),
                    background = R.drawable.bg_background_oxygen,
                    heart_rate = 12,
                    timestamp = System.currentTimeMillis()
                )
            ),
            HealthItem.Temperature(
                temperature = TemperatureBody(
                    icon = R.drawable.icc_thermometer,
                    name = context.resources.getString(R.string.text_categories_temperature),
                    message = context.resources.getString(R.string.text_content_categories_temperature),
                    background = R.drawable.bg_background_temperature,
                    temperature = 12f,
                    timestamp = System.currentTimeMillis()
                )
            ),
            HealthItem.Stress(
                stress = Stress(
                    icon = R.drawable.icc_leaves,
                    name = context.resources.getString(R.string.text_categories_stress),
                    message = context.resources.getString(R.string.text_content_categories_stress),
                    background = R.drawable.img_stress,
                    stressScore = 30,
                    timestamp = System.currentTimeMillis()
                )
            ),
            HealthItem.Oxygen(
                oxygen = BloodOxygen(
                    icon = R.drawable.icc_leaves,
                    name = context.resources.getString(R.string.text_categories_oxygen),
                    message = context.resources.getString(R.string.text_content_categories_oxygen),
                    background = R.drawable.bg_background_oxygen,
                    oxygenLevel = 20,
                    timestamp = System.currentTimeMillis()
                )
            )
        )
    }
    fun listTag(context: Context):List<String>
    {
        return listOf(
            context.resources.getString(R.string.text_categories_oxygen),
            context.resources.getString(R.string.text_categories_congtaguration),
            context.resources.getString(R.string.text_categories_stress),
            context.resources.getString(R.string.text_categories_temperature),
            context.resources.getString(R.string.text_categories_heart_rate),
            context.resources.getString(R.string.text_categories_sleep),
        )
    }
    fun listMobile(context: Context):List<String>
    {
        return listOf(
            "Samsung One UI NEW",
            "Huawei EMUI",
            "OPPO ColorOS",
            "Vivo Funtouch OS",
            "Other phones"
        )
    }
    fun listFaq(context: Context) : MutableList<Faq>
    {
        return mutableListOf(
            Faq(
                question = context.resources.getString(R.string.faq_question_1),
                answer = context.resources.getString(R.string.faq_answer_1),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_2),
                answer = context.resources.getString(R.string.faq_answer_2),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_3),
                answer = context.resources.getString(R.string.faq_answer_3),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_4),
                answer = context.resources.getString(R.string.faq_answer_4),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_5),
                answer = context.resources.getString(R.string.faq_answer_5),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_6),
                answer = context.resources.getString(R.string.faq_answer_6),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_7),
                answer = context.resources.getString(R.string.faq_answer_7),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_8),
                answer = context.resources.getString(R.string.faq_answer_8),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_9),
                answer = context.resources.getString(R.string.faq_answer_9),
            ),
            Faq(
                question = context.resources.getString(R.string.faq_question_10),
                answer = context.resources.getString(R.string.faq_answer_10),
            )
        )
    }

}