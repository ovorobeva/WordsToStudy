package com.github.ovorobeva.wordstostudy;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import static com.github.ovorobeva.wordstostudy.AppWidget.TAG;
import static com.github.ovorobeva.wordstostudy.AppWidget.updateAppWidget;
import static com.github.ovorobeva.wordstostudy.Preferences.IS_COLOR_CHANGED;
import static com.github.ovorobeva.wordstostudy.Preferences.IS_PERIOD_CHANGED;
import static com.github.ovorobeva.wordstostudy.Preferences.IS_WORD_COUNT_CHANGED;
import static com.github.ovorobeva.wordstostudy.Preferences.PERIOD;
import static com.github.ovorobeva.wordstostudy.Preferences.WORDS_COUNT;
import static com.github.ovorobeva.wordstostudy.Preferences.arePrefsEmpty;
import static com.github.ovorobeva.wordstostudy.Preferences.loadColorFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadSettingFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.saveSettingToPref;
import static com.github.ovorobeva.wordstostudy.Preferences.saveWordsColorToPref;

/**
 * The configuration screen for the {@link AppWidget NewAppWidget} AppWidget.
 */
public class ConfigureActivity extends Activity {

    static final int EVERY_DAY = 1;
    static final int EVERY_THREE_DAYS = 3;
    static final int EVERY_MONDAY = 7;

//    private final Preferences preferences = Preferences.getPreferences(ConfigureActivity.this);

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private int period;
    private int color;
    private int wordCount;


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ConfigureActivity.this;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            boolean isPeriodChanged = false;
            boolean isColorChanged = false;
            boolean isWordCountChanged = false;

            Log.d(TAG, "onClick: prefs are empty: " + arePrefsEmpty(context));

            if (!arePrefsEmpty(context)) {
                if (loadColorFromPref(mAppWidgetId, context) != color) isColorChanged = true;
                if (loadSettingFromPref(Preferences.WORDS_COUNT, context) != wordCount)
                    isWordCountChanged = true;
                if (loadSettingFromPref(Preferences.PERIOD, context) != period)
                    isPeriodChanged = true;
            } else {
                isPeriodChanged = true;
                isColorChanged = true;
                isWordCountChanged = true;
            }
            Log.d(TAG, "Configure Activity.onClick: isColorChanged = " + isColorChanged);
            Log.d(TAG, "Configure Activity.onClick: isWordCountChanged = " + isWordCountChanged);
            Log.d(TAG, "Configure Activity.onClick: isPeriodChanged = " + isPeriodChanged);

            saveSettingToPref(period, Preferences.PERIOD, context);
            saveSettingToPref(wordCount, Preferences.WORDS_COUNT, context);
            saveWordsColorToPref(color, mAppWidgetId, context);

            Log.d(TAG, "onClick: settings saved. New values are: \n period: " + loadSettingFromPref(PERIOD, context)
                    + "\n words count: " + loadSettingFromPref(WORDS_COUNT, context)
                    + "\n color: " + loadColorFromPref(mAppWidgetId, context));
            int changed = isColorChanged ? 1 : 0;
            saveSettingToPref(changed, IS_COLOR_CHANGED, context);
            changed = isWordCountChanged ? 1 : 0;
            saveSettingToPref(changed, IS_WORD_COUNT_CHANGED, context);
            changed = isPeriodChanged ? 1 : 0;
            saveSettingToPref(changed, IS_PERIOD_CHANGED, context);

            updateAppWidget(context, appWidgetManager, mAppWidgetId);
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


        Spinner wordsCountText = findViewById(R.id.words_count_edit_text);
        String[] items = new String[]{"3", "5", "10"};

        wordsCountText.setSelection(0);


        RadioButton checkedRadioButton;
        checkedRadioButton = findViewById(R.id.every_day);
        checkedRadioButton.setChecked(true);

        checkedRadioButton = findViewById(R.id.black_text);
        checkedRadioButton.setChecked(true);


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


//todo: to set other options as default
    }

    @Override
    public void onResume() {
        super.onResume();
        color = loadColorFromPref(mAppWidgetId, ConfigureActivity.this);
        period = loadSettingFromPref(Preferences.PERIOD, ConfigureActivity.this);
        wordCount = loadSettingFromPref(Preferences.WORDS_COUNT, ConfigureActivity.this);

        Spinner wordsCountText = findViewById(R.id.words_count_edit_text);

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

        if (loadColorFromPref(mAppWidgetId, ConfigureActivity.this) == Color.BLACK)
            checkedRadioButton = findViewById(R.id.black_text);
        else checkedRadioButton = findViewById(R.id.white_text);
        checkedRadioButton.setChecked(true);

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
