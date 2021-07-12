package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Words {

    private static final String TAG = "Custom logs";
    private static List<GeneratedWords> wordsToProcess = new LinkedList<>();

    public static synchronized void setWords(RemoteViews views, Preferences preferences) {
        Log.d(TAG, "setWords: setWords is called: 1");
        StringBuilder words = new StringBuilder();

        int wordsCount;
        wordsCount = preferences.loadWordsCountFromPref();

        WordsClient wordsClient = WordsClient.getWordsClient();
        List<String> processedResult = new LinkedList<>();

        Log.d(TAG, "setWords: calling getwords: 2");
        wordsClient.getWords(wordsToProcess);
        Log.d(TAG, "setWords: Words to process are: " + wordsToProcess);
        processResponse(wordsCount, wordsToProcess, processedResult);

        for (String s : processedResult) {
            words.append(s).append("\n");
        }

        views.setTextViewText(R.id.appwidget_text, words);
        Log.d(TAG, "setWords: Text updated. New text is: " + words);

        }


    private static void processResponse(int wordsCount, List<GeneratedWords> response, List<String> processedResult) {
        if (response.isEmpty()) return;
        Random random = new Random();
        List<GeneratedWords> randomWords = new LinkedList<>();
        int id;
        for (int i = 0; i < wordsCount; i++){
            id = random.nextInt(response.size());
            if (id > 0) id--;
            randomWords.add(response.get(id));
        }

        for (GeneratedWords generatedWord : randomWords) {
            processedResult.add(generatedWord.getEn() + " - " + generatedWord.getRu());
    }
        Log.d(TAG, "processResponse: processed result is: " + processedResult);

}}

