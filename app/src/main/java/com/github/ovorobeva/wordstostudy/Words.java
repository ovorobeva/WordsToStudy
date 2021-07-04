package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Words {

    private static final String TAG = "Custom logs";
    private static List<GeneratedWords> wordsToProcess = new LinkedList<>();

    public static void setWords(Context context) {
        StringBuilder words = new StringBuilder();

        int wordsCount;
        wordsCount = ConfigureActivity.loadWordsCountFromPref(context);

        WordsClient wordsClient = WordsClient.getWordsClient();
        List<String> processedResult = new LinkedList<>();

        wordsClient.getWords(wordsToProcess);
        Log.d(TAG, "getWords: Words to process are: " + wordsToProcess);
        processResponse(wordsCount, wordsToProcess, processedResult);

        for (String s : processedResult) {
            words.append(s).append("\n");
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.appwidget_text, words);

        }


    private static void processResponse(int wordsCount, List<GeneratedWords> response, List<String> processedResult) {
        Random random = new Random();
        List<GeneratedWords> randomWords = new LinkedList<>();
        for (int i = 0; i < wordsCount; i++){
            randomWords.add(response.get(random.nextInt(response.size() - 1)));
        }


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONArray wordsArray = new JSONArray();

        for (GeneratedWords generatedWord : randomWords) {
            JSONObject word = null;
            try {
                word = new JSONObject(gson.toJson(generatedWord));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            wordsArray.put(word);
        }
        Log.d(TAG, "processResponse: processed result is: " + processedResult);
    }

}

