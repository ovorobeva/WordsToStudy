package com.github.ovorobeva.wordstostudy;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * The configuration screen for the {@link AppWidget NewAppWidget} AppWidget.
 */
public class ConfigureActivity extends Activity {

    static final int EVERY_DAY = 5000;
    static final int EVERY_THREE_DAYS = 10000;
    static final int EVERY_MONDAY = 15000;
    private final Preferences preferences = Preferences.getPreferences(ConfigureActivity.this);
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    //todo: add other options to save in configurations (period, color, size, languages etc.)
    private int period;


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ConfigureActivity.this;
            TextView appwidget_text = findViewById(R.id.appwidget_text);
            int count = Integer.parseInt(appwidget_text.getText().toString());

            //todo: to make widget to be shown on the  main screen
            //savePeriodToPref(context, mAppWidgetId, period); The example how to use widget id in prefs
            preferences.savePeriodToPref(period);
            preferences.saveWordsCountToPref(count);
            preferences.saveIdToPref(mAppWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

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

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_configure);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
//todo: to set other options as default
        period = preferences.loadPeriodFromPref();
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
