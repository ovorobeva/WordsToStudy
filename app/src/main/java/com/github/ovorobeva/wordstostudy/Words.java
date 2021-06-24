package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Words {

    private static final String TAG = "Custom logs";
    private static final String WORD = "word";
    private static final String PART_OF_SPEECH = "partOfSpeech";
    private static volatile StringBuilder wordsToProcess = new StringBuilder();
    private static volatile StringBuilder partsOfSpeech = new StringBuilder();

    public static synchronized void getWords(Context context, List<String> words) {


        int wordsCount;
        wordsCount = ConfigureActivity.loadWordsCountFromPref(context);

        WordsClient wordsClient = WordsClient.getWordsClient();
        List<String> processedResult = new LinkedList<>();

        wordsClient.getRandomWords(wordsCount, wordsToProcess);
        Log.d(TAG, "getWords: Words to process are: " + wordsToProcess);
        processResponse(wordsCount, WORD, wordsToProcess, processedResult);

        Iterator<String> iterator = processedResult.iterator();
        int removedCounter = 0;
        Log.d(TAG, "getWords: Starting removing non-matching words from the list \n" + processedResult);

        while (iterator.hasNext()) {
            String word = iterator.next();
            Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");
            Matcher matcher = pattern.matcher(word);

            if (matcher.find()) {
                iterator.remove();
                removedCounter++;
                Log.d(TAG, "getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult() + ". The count of deleted words is " + removedCounter);
                continue;
            }

            if (!isPartOfSpeechCorrect(word, wordsClient)) {
                iterator.remove();
                removedCounter++;
                Log.d("Custom logs", "getWords: Removing the word " + word + " because of the wrong part of speech. The count of deleted words is " + removedCounter);

            }

        }
        words.clear();
        words.addAll(processedResult);

    }

    private static synchronized Boolean isPartOfSpeechCorrect(String word, WordsClient wordsClient) {
        Log.d(TAG, "isPartOfSpeechCorrect: the word " + word + " is being checked");
        boolean isCorrect = true;
        wordsClient.getPartsOfSpeech(word, partsOfSpeech);
        Log.d(TAG, "isPartOfSpeechCorrect: Parts of speech for the word " + word + " are received. POS are: " + partsOfSpeech);

        List<String> processedResult = new LinkedList<>();
        processResponse(0, PART_OF_SPEECH, partsOfSpeech, processedResult);

        Log.d(TAG, "isPartOfSpeechCorrect: parts of speech for the word " + word + " are: " + processedResult);

        for (String partOfSpeech : processedResult) {
            if (!partOfSpeech.matches("(?i)noun|adjective|adverb|idiom|past-participle")) {
                Log.d(TAG, "isPartOfSpeechCorrect: The word " + word + "is to be removed because of part of speech: " + partOfSpeech);
                isCorrect = false;
                break;
            }
        }
        return isCorrect;
    }


    private static synchronized void processResponse(int wordsCount, String entity, StringBuilder response, List<String> processedResult) {
        List<String> responseList = new LinkedList<>();
        try {
            JSONArray jsonResponse = new JSONArray(response.toString());
            if (wordsCount == 0) wordsCount = jsonResponse.length();
            for (int i = 0; i < wordsCount; i++) {
                responseList.add(jsonResponse.getJSONObject(i).getString(entity));
            }
            Log.d(TAG, "processResponse: response processed");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "processResponse: Cannot parse response.Response is: " + response + " Error message is: ", e);
        }
        processedResult.clear();
        processedResult.addAll(responseList);
        Log.d(TAG, "processResponse: processed result is: " + processedResult);
    }

}

