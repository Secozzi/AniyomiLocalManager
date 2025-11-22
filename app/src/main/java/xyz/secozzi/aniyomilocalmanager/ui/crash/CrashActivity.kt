package xyz.secozzi.aniyomilocalmanager.ui.crash

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.secozzi.aniyomilocalmanager.BuildConfig
import xyz.secozzi.aniyomilocalmanager.MainActivity
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.crash.CrashScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.theme.AniyomiLocalManagerTheme
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class CrashActivity : ComponentActivity() {

    private val clipboardManager by lazy { getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }
    private lateinit var logcat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        lifecycle.coroutineScope.launch {
            logcat = collectLogcat()
        }
        val exceptionString = intent.getStringExtra("exception") ?: ""
        setContent {
            AniyomiLocalManagerTheme {
                val scope = rememberCoroutineScope()
                CrashScreenContent(
                    exceptionString = exceptionString,
                    logcat = logcat,
                    onShare = {
                        scope.launch {
                            shareLogs(exceptionString, logcat)
                        }
                    },
                    onCopy = {
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(
                                null,
                                concatLogs(collectDeviceInfo(), exceptionString, logcat),
                            ),
                        )
                    },
                    onRestart = {
                        finish()
                        startActivity(Intent(this@CrashActivity, MainActivity::class.java))
                    },
                )
            }
        }
    }

    private fun collectLogcat(): String {
        val process = Runtime.getRuntime()
        val reader = BufferedReader(InputStreamReader(process.exec("logcat -d").inputStream))
        val logcat = StringBuilder()
        reader.lines().forEach(logcat::appendLine)
        // clear logcat so it doesn't pollute subsequent crashes
        process.exec("logcat -c")
        return logcat.toString()
    }

    private fun concatLogs(
        deviceInfo: String,
        crashLogs: String,
        logcat: String,
    ): String {
        return """
          $deviceInfo

          Exception:
          $crashLogs

          Logcat:
          $logcat
        """.trimIndent()
    }

    private suspend fun shareLogs(
        exceptionString: String,
        logcat: String,
    ) {
        withContext(NonCancellable) {
            val file = File(applicationContext.cacheDir, "aniyomi_local_manager_logs.txt")
            if (file.exists()) file.delete()
            file.createNewFile()
            file.appendText(concatLogs(collectDeviceInfo(), exceptionString, logcat))
            val uri = FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                clipData = ClipData.newRawUri(null, uri)
                type = "text/plain"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            this@CrashActivity.startActivity(
                Intent.createChooser(intent, applicationContext.getString(R.string.crash_screen_share)),
            )
        }
    }

    private fun collectDeviceInfo(): String {
        return """
        App version: ${BuildConfig.VERSION_NAME}
        Android version: ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})
        Device brand: ${Build.BRAND}
        Device manufacturer: ${Build.MANUFACTURER}
        Device model: ${Build.MODEL} (${Build.DEVICE})
        """.trimIndent()
    }
}
