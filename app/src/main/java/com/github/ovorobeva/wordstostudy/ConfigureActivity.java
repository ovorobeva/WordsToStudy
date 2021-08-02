package com.github.ovorobeva.wordstostudy;

import android.app.Activity;
import android.app.AlarmManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * The configuration screen for the {@link AppWidget NewAppWidget} AppWidget.
 */
public class ConfigureActivity extends Activity {

    static final int EVERY_DAY = 1;
    static final int EVERY_THREE_DAYS = 3;
    static final int EVERY_MONDAY = 7;
    private final Preferences preferences = Preferences.getPreferences(ConfigureActivity.this);
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    //todo: add other options to save in configurations (period, color, size, languages etc.)
    private int period;
    private int color;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ConfigureActivity.this;

            //todo: to make widget to be shown on the  main screen
            preferences.savePeriodToPref(period);
            preferences.saveWordsCountToPref(wordCount);
            preferences.saveWordsColorToPref(color, mAppWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, preferences);
            AppWidget.updateTextAppWidget(context, appWidgetManager);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    private int wordCount;


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

        Spinner wordsCountText = findViewById(R.id.words_count_edit_text);
        String[] items = new String[]{"3", "5", "10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        wordsCountText.setAdapter(adapter);
        wordsCountText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        wordCount = 5;
                        break;
                    case 2:
                        wordCount = 10;
                        break;
                    default:
                        wordCount = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        period = preferences.loadFromPref(Preferences.PERIOD);
        wordCount = preferences.loadFromPref(Preferences.WORDS_COUNT);
        if (wordCount == 0) wordCount = 3;

        RadioButton checkedRadioButton;
        switch (period) {
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

        if (preferences.loadColorFromPref(mAppWidgetId) == Color.BLACK)
            checkedRadioButton = findViewById(R.id.black_text);
        else checkedRadioButton = findViewById(R.id.white_text);
        checkedRadioButton.setChecked(true);

        switch (wordCount) {
            case 5:
                wordsCountText.setSelection(1);
                break;
            case 10:
                wordsCountText.setSelection(2);
                break;
            default:
                wordsCountText.setSelection(0);
        }
//todo: to set other options as default
    }

    @Override
    public void onResume() {
        super.onResume();
        //todo: to restore the previous state (to watch the lesson again)
        Log.d(AppWidget.class.getCanonicalName() + ".onResume", "Config activity for the widget ID " + mAppWidgetId + " is opened");

    }

    public void radioButtonColorClickListener(View view) {
        RadioButton checkedRadioButton = (RadioButton) view;
        switch (checkedRadioButton.getId()) {
            case R.id.white_text:
                color = Color.WHITE;
                break;
            case R.id.black_text:
                color = Color.BLACK;
        }
    }

    public void radioButtonPeriodClickListener(View view) {
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
