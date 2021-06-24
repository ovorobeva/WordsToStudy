package com.github.ovorobeva.wordstostudy;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WordsClient {
    private static final Object OBJECT = new Object();
    private static final String TAG = "Custom logs";
    private volatile static WordsClient wordsClient;
    private final String BASE_URL = "https://api.wordnik.com/v4/";
    private final String API_KEY = "55k0ykdy6pe8fmu69pwjk94es02i9085k3h1hn11ku56c4qep";
    private final WordsApi wordsApi;

    private WordsClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        wordsApi = retrofit.create(WordsApi.class);
    }

    public static WordsClient getWordsClient() {
        if (wordsClient != null)
            return wordsClient;

        synchronized (OBJECT) {
            if (wordsClient == null)
                wordsClient = new WordsClient();
            return wordsClient;
        }
    }

    public void getRandomWords(int wordsCount, StringBuilder responseToProcess) {

        Map<String, String> apiVariables = new HashMap<>();
        apiVariables.put("minCorpusCount", "100000");
        apiVariables.put("maxCorpusCount", "-1");
        apiVariables.put("minDictionaryCount", "0");
        apiVariables.put("maxDictionaryCount", "-1");
        apiVariables.put("minLength", "2");
        apiVariables.put("maxLength", "-1");

        List<String> includePartOfSpeechList = new ArrayList<>();
        includePartOfSpeechList.add("noun,");
        includePartOfSpeechList.add("adjective,");
        includePartOfSpeechList.add("verb,");
        includePartOfSpeechList.add("idiom,");
        includePartOfSpeechList.add("past-participle");

        StringBuilder includePartOfSpeech = new StringBuilder();

        for (String partOfSpeech : includePartOfSpeechList) {
            includePartOfSpeech.append(partOfSpeech);
        }

        List<String> excludePartOfSpeechList = new ArrayList<>();
        excludePartOfSpeechList.add("interjection,");
        excludePartOfSpeechList.add("pronoun,");
        excludePartOfSpeechList.add("preposition,");
        excludePartOfSpeechList.add("abbreviation,");
        excludePartOfSpeechList.add("affix,");
        excludePartOfSpeechList.add("article,");
        excludePartOfSpeechList.add("auxiliary-verb,");
        excludePartOfSpeechList.add("conjunction,");
        excludePartOfSpeechList.add("definite-article,");
        excludePartOfSpeechList.add("family-name,");
        excludePartOfSpeechList.add("given-name,");
        excludePartOfSpeechList.add("imperative,");
        excludePartOfSpeechList.add("proper-noun,");
        excludePartOfSpeechList.add("proper-noun-plural,");
        excludePartOfSpeechList.add("suffix,");
        excludePartOfSpeechList.add("verb-intransitive,");
        excludePartOfSpeechList.add("verb-transitive");


        StringBuilder excludePartOfSpeech = new StringBuilder();

        for (String partOfSpeech : excludePartOfSpeechList) {
            excludePartOfSpeech.append(partOfSpeech);
        }

        Call<String> randomWords = wordsApi.sendRequest(true, includePartOfSpeech.toString(), excludePartOfSpeech.toString(), apiVariables.get("minCorpusCount"),
                apiVariables.get("maxCorpusCount"), apiVariables.get("minDictionaryCount"), apiVariables.get("maxDictionaryCount"),
                apiVariables.get("minLength"), apiVariables.get("maxLength"), String.valueOf(wordsCount), API_KEY);

        enqueue(randomWords, responseToProcess);

        Log.d(TAG, "getRandomWords. Response to process is: " + responseToProcess);
        //    return responseToProcess;

    }

    public synchronized void getPartsOfSpeech(String word, StringBuilder responseToProcess) {
        Log.d(TAG, "getPartsOfSpeech: Start getting parts of speech for the word " + word);
        final String LIMIT = "500";
        Map<String, Boolean> apiVariables = new HashMap<>();
        apiVariables.put("includeRelated", false);
        apiVariables.put("useCanonical", false);
        apiVariables.put("includeTags", false);


        Call<String> partsOfSpeech = wordsApi.sendRequest(word, apiVariables.get("includeRelated"),
                apiVariables.get("useCanonical"), apiVariables.get("includeTags"), LIMIT, API_KEY);

        enqueue(partsOfSpeech, responseToProcess);

    }

    private synchronized void enqueue(Call<String> call, StringBuilder responseToProcess) {

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    responseToProcess.setLength(0);
                    responseToProcess.append(response.body());
                    Log.d(TAG, "onResponse: Response by the url " + response.raw().request().url() + " is received.\nResponse to process is: " + responseToProcess);
                } else {
                    Log.e(TAG, "onResponse: There is an error during request. Response code is: " + response.code() + " url is: " + response.raw().request().url());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong during request by url " + call.request().url(), t);
                t.printStackTrace();
            }
        });
    }


}