package com.github.ovorobeva.wordstostudy;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * The configuration screen for the {@link AppWidget NewAppWidget} AppWidget.
 */
public class ConfigureActivity extends Activity {
    //todo: to get rid of excess comments
    //todo: to make logger
    //path to the prefs on the device
    static final String PREFS_NAME = "com.github.ovorobeva.wordstostudy.NewAppWidget";
    //prefix to show with the setting name to separate settings for more than one widgets
    // /data/user/0/com.github.ovorobeva.wordstostudy/shared_prefs/com.github.ovorobeva.wordstostudy.NewAppWidget.xml
    static final String PREF_PREFIX_KEY = "appwidget_";

    private static final int EVERY_DAY = 5000;
    private static final int EVERY_THREE_DAYS = 10000;
    private static final int EVERY_MONDAY = 15000;
    private static int count = 3;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    //todo: add other options to save in configurations (period, color, size, languages etc.)

    private int period;


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ConfigureActivity.this;
            TextView appwidget_text = findViewById(R.id.appwidget_text);
            count = Integer.parseInt(appwidget_text.getText().toString());

            // When the button is clicked, store the string locally
            //todo: to rewrite getWords() to throw the API-request and remove it into update
            //savePeriodToPref(context, mAppWidgetId, period); The example how to use widget id in prefs
            savePeriodToPref(context, period);
            saveWordsCountToPref(context, count);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public ConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    //The example how to make prefs for the definite widget (using widgetID)
/*    static void savePeriodToPref(Context context, int appWidgetId, int period) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, period);
        prefs.apply();
    }*/

    static void savePeriodToPref(Context context, int period) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY, period);
        prefs.apply();
    }
    static void saveWordsCountToPref(Context context, int count) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY, count);
        prefs.apply();
    }
    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    //todo: to load other preferences like title value
    static int loadPeriodFromPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY, EVERY_DAY);
    }

    //Here is the example how to use prefs according to the definite widget id
  /*  static int loadPeriodFromPref(Context context, int mAppWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + mAppWidgetId, EVERY_DAY);
    }*/
    static int loadWordsCountFromPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY, 1);
    }

    //todo: to delete other preferences like title value
    static void deletePeriodFromPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_configure);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
//todo: to set other options as default
        period = loadPeriodFromPref(ConfigureActivity.this);
    }
    @Override
    public void onResume() {
        super.onResume();
        //todo: to restore the previous state (to watch the lesson again)
        Log.d(AppWidget.class.getCanonicalName() + ".onResume", "Config activity for the widget ID " + mAppWidgetId + " is opened");

    }

    public void radioButtonClickListener(View view) {
        RadioButton checkedRadioButton = (RadioButton) view;
        switch (checkedRadioButton.getId()) {
            case R.id.every_day:
                period = EVERY_DAY;
                break;
            case R.id.every_three_days:
                period = EVERY_THREE_DAYS;
                break;
            case R.id.every_monday:
                period = EVERY_MONDAY;
        }
    }
}
