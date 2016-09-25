package com.tasomaniac.betachecker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareToCheckBeta extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ShareCompat.IntentReader reader = ShareCompat.IntentReader.from(this);
        String appId = extractPlayIdFrom(getIntent(), reader);

        if (appId == null) {
            Toast.makeText(this, R.string.error_invalid_url, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.apps_primary))
                .build();
        intent.launchUrl(this, Uri.parse("https://play.google.com/apps/testing/" + appId));
        finish();
    }

    private static String extractPlayIdFrom(Intent intent, ShareCompat.IntentReader reader) {
        CharSequence text = reader.getText();
        if (text == null) {
            text = getExtraSelectedText(intent);
        }
        return findPlayAppId(text);
    }

    @SuppressLint("InlinedApi")
    private static CharSequence getExtraSelectedText(Intent intent) {
        return intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
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
}
