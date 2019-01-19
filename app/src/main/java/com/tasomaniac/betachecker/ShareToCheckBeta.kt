package com.tasomaniac.betachecker

import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.core.app.ShareCompat.IntentReader
import androidx.core.content.ContextCompat
import java.util.regex.Pattern

class ShareToCheckBeta : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reader = IntentReader.from(this)
        val appId = extractPlayIdFrom(reader)

        if (appId == null) {
            Toast.makeText(this, R.string.error_invalid_url, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val intent = Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.apps_primary))
                .build()
                .intent
        intent.data = Uri.parse("https://play.google.com/apps/testing/$appId")
        intent.setPackage(findBestBrowserPackage())
        startActivity(intent)
        finish()
    }

    private fun findBestBrowserPackage(): String? {
        if (isPackageInstalled("com.android.chrome"))
            return "com.android.chrome"
        val browsers = queryBrowsers()
        return if (browsers.isEmpty())
            null else browsers[0].activityInfo.packageName
    }

    private fun queryBrowsers(): List<ResolveInfo> {
        val browserIntent = Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse("http:"))
        return packageManager.queryIntentActivities(browserIntent, 0)
    }

    private fun extractPlayIdFrom(reader: IntentReader): String? {
        return findPlayAppId(reader.text)
    }

    private fun findPlayAppId(text: CharSequence?): String? {
        if (text == null)
            return null
        val matcher = Pattern.compile(
                "https://play\\.google\\.com/store/apps/details\\?id=(.+)", Pattern.CASE_INSENSITIVE)
                .matcher(text)
        return if (matcher.find())
            matcher.group(1)
        else null
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getApplicationInfo(packageName, 0).enabled
        } catch (e: NameNotFoundException) {
            false
        }
    }
}
