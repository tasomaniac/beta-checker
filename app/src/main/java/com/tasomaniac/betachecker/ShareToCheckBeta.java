package com.tasomaniac.betachecker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareToCheckBeta extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ShareCompat.IntentReader reader = ShareCompat.IntentReader.from(this);
        String appId = extractPlayIdFrom(reader);

        if (appId == null) {
            Toast.makeText(this, R.string.error_invalid_url, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent i = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.apps_primary))
                .build()
                .i;
        i.setData(Uri.parse("https://play.google.com/apps/testing/" + appId));
        i.setPackage(findBestBrowserPackage());
        startActivity(i);
        finish();
    }

    @Nullable
    private String findBestBrowserPackage() {
        if (isPackageInstalled("com.android.chrome")) {
            return "com.android.chrome";
        }
        List<ResolveInfo> browsers = queryBrowsers();
        if (browsers.isEmpty()) {
            return null;
        }
        return browsers.get(0).activityInfo.packageName;
    }

    private List<ResolveInfo> queryBrowsers() {
        Intent browserIntent = new Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse("http:"));
        return getPackageManager().queryIntentActivities(browserIntent, 0);
    }

    private static String extractPlayIdFrom(ShareCompat.IntentReader reader) {
        return findPlayAppId(reader.getText());
    }

    @Nullable
    private static String findPlayAppId(@Nullable CharSequence text) {
        if (text == null) {
            return null;
        }
        final Matcher matcher = Pattern.compile("https://play\\.google\\.com/store/apps/details\\?id=(.+)", Pattern.CASE_INSENSITIVE)
                .matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean isPackageInstalled(String packageName) {
        try {
            return getPackageManager().getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
