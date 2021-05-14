package com.example.webapp.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * App Utils
 * @author yeliulee
 * Created at 2021/5/14 18:07
 */
object AppUtils {
    fun getFromAssets(context: Context, fileName: String?): String? {
        try {
            val inputStreamReader = InputStreamReader(
                context.resources.assets.open(
                    fileName!!
                )
            )
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String?
            var result: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) result += line
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}