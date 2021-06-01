package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.util.Log;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Words {

    private static final String TAG = "Custom logs";

    public static void getWords(Context context, List<String> words) {

        int wordsCount;
        wordsCount = ConfigureActivity.loadWordsCountFromPref(context);

        WordsClient wordsClient = new WordsClient();
        List<String> wordsToProcess = new LinkedList<>(wordsClient.getRandomWords(wordsCount));

        Iterator<String> iterator = wordsToProcess.iterator();
        int removedCounter = 0;
        Log.d(TAG, "getWords: Starting removing non-matching words from the list \n" + wordsToProcess);

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
        words.addAll(wordsToProcess);

    }

    private static Boolean isPartOfSpeechCorrect(String word, WordsClient wordsClient) {
        boolean isCorrect = true;
        List<String> partsOfSpeech = new LinkedList<>(wordsClient.getPartsOfSpeech(word));
        Log.d(TAG, "isPartOfSpeechCorrect: parts of speech for the word " + word + "are: " + partsOfSpeech);

        for (String partOfSpeech : partsOfSpeech) {
            if (!partOfSpeech.matches("(?i)noun|adjective|adverb|idiom|past-participle")) {
                Log.d(TAG, "isPartOfSpeechCorrect: The word " + word + "is to be removed because of part of speech: " + partOfSpeech);
                isCorrect = false;
                break;
            }
        }
        return isCorrect;
    }

}

