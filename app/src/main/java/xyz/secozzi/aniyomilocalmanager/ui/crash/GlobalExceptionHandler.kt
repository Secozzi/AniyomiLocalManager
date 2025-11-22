package xyz.secozzi.aniyomilocalmanager.ui.crash

import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess

class GlobalExceptionHandler(
    private val context: Context,
    private val activity: Class<*>,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        val intent = Intent(context, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("exception", e.stackTraceToString())
        }
        context.startActivity(intent)
        exitProcess(0)
    }
}
