package com.github.ovorobeva.wordstostudy;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
            TextView appwidget_text = findViewById(R.id.words_count_edit_text);
            int count = Integer.parseInt(appwidget_text.getText().toString());

            //todo: to make widget to be shown on the  main screen
            preferences.saveIdToPref(mAppWidgetId);
            preferences.savePeriodToPref(period);
            preferences.saveWordsCountToPref(count);

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

        period = preferences.loadFromPref(Preferences.PERIOD);
        int wordCount = preferences.loadFromPref(Preferences.WORDS_COUNT);
        if (wordCount == 0) wordCount = 3;

        RadioButton checkedRadioButton;
        switch (period) {
            case EVERY_DAY:
                checkedRadioButton = findViewById(R.id.every_day);
                break;
            case EVERY_MONDAY:
                checkedRadioButton = findViewById(R.id.every_monday);
                break;
            case EVERY_THREE_DAYS:
                checkedRadioButton = findViewById(R.id.every_three_days);
                break;
            default:
                checkedRadioButton = findViewById(R.id.every_day);

        }
        checkedRadioButton.setChecked(true);

        EditText wordsCountText = findViewById(R.id.words_count_edit_text);
        wordsCountText.setText(String.valueOf(wordCount));
//todo: to set other options as default
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
