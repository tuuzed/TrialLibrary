package com.github.tuuzed.libtrial

import android.content.Context
import androidx.annotation.WorkerThread
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

internal object TrialUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    fun init(ctx: Context) {
        thread {
            val expireDate = expireDate(ctx.packageName) ?: return@thread
            val nowDate = nowDate()
            if (nowDate.after(expireDate)) {
                exitProcess(0)
            }
        }
    }

    @WorkerThread
    private fun expireDate(packageName: String): Date? {
        return kotlin.runCatching {
            val source = httpGet(
                "https://gitee.com/openpages/trial/raw/$packageName/date"
            ) ?: return null
            dateFormat.parse(source)
        }.getOrNull()
    }

    @WorkerThread
    private fun nowDate(): Date {
        var date = kotlin.runCatching { taobaoDate() }.getOrNull()
        if (date != null) {
            return date
        }
        date = kotlin.runCatching { suningDate() }.getOrNull()
        if (date != null) {
            return date
        }
        return Date()
    }

    @WorkerThread
    @Throws(Exception::class)
    private fun taobaoDate(): Date? {
        val source = httpGet(
            "https://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp"
        ) ?: return null
        val jo = JSONObject(source)
        return jo.getJSONObject("data").getString("t").toLongOrNull()?.let { Date(it) }
    }

    @WorkerThread
    @Throws(Exception::class)
    private fun suningDate(): Date? {
        val source = httpGet(
            "https://quan.suning.com/getSysTime.do"
        ) ?: return null
        val jo = JSONObject(source)
        return jo.getString("sysTime2").let { dateFormat.parse(it) }
    }

    @WorkerThread
    @Throws(Exception::class)
    private fun httpGet(url: String): String? {
        return kotlin.runCatching {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connect()
            conn.inputStream.bufferedReader().readText()
        }.getOrNull()
    }

}