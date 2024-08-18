package indi.dmzz_yyhyy.lightnovelreader.utils

import android.content.Context
import java.util.Locale

object LocaleUtil {
    private fun updateResource(context: Context, language: String, variant: String){
        context.resources.apply {
            Locale.setDefault(Locale(language, variant))
        }
    }

    fun set(context: Context, language: String, variant: String) = updateResource(
        context = context,
        language = language,
        variant = variant
    )
}