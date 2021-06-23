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
    private static final String TAG = "Custom logs";
    private final String BASE_URL = "https://api.wordnik.com/v4/";
    private final String API_KEY = "55k0ykdy6pe8fmu69pwjk94es02i9085k3h1hn11ku56c4qep";
    private final String WORD = "word";
    private final String PART_OF_SPEECH = "partOfSpeech";
    WordsApi wordsApi;

    static WordsClient wordsClient;

    public static synchronized WordsClient getWordsClient(){
        if (wordsClient == null){
            wordsClient = new WordsClient();
        }
        return wordsClient;
    }

    private WordsClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
               // .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        wordsApi = retrofit.create(WordsApi.class);
    }


    List<String> getRandomWords(int wordsCount) {
        List<String> processedResult = new LinkedList<>();

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

/*        try {
            Response<JSONArray> response = randomWords.execute();
            Log.d(TAG, "getRandomWords: " + response.body());
        } catch (IOException e) {
            Log.d(TAG, "getPartsOfSpeech: Something went wrong during request");
            e.printStackTrace();
        }*/

        randomWords.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    processResponse(wordsCount, WORD, response.body(), processedResult);
                    Log.d(TAG, "getRandomWords: " + response.body());
                } else {
                    Log.e(TAG, "getRandomWords: There is an error during request. Response code is: " + response.code() + "url is: " + response.raw().request().url());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "getRandomWords: Something went wrong during request");
                t.printStackTrace();

            }
        });


        return processedResult;

    }

    public List<String> getPartsOfSpeech(String word) {
        List<String> processedResult = new LinkedList<>();
        final String LIMIT = "500";
        Map<String, Boolean> apiVariables = new HashMap<>();
        apiVariables.put("includeRelated", false);
        apiVariables.put("useCanonical", false);
        apiVariables.put("includeTags", false);


        Call<String> partsOfSpeech = wordsApi.sendRequest(word, apiVariables.get("includeRelated"),
                apiVariables.get("useCanonical"), apiVariables.get("includeTags"), LIMIT, API_KEY);


        partsOfSpeech.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    processResponse(0, PART_OF_SPEECH, response.body(), processedResult);
                    Log.d(TAG, "getPartsOfSpeech: " + response.body());
                } else {
                    Log.e(TAG, "getPartsOfSpeech: There is an error during request. Response code is: " + response.code() + "url is: " + response.raw().request().url());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "getPartsOfSpeech: Something went wrong during request");
                t.printStackTrace();

            }
        });

        return processedResult;


    }


    private void processResponse(int wordsCount, String entity, String response, List<String> processedResult) {
        List<String> responseList = new LinkedList<>();
        try {
        JSONArray jsonResponse = new JSONArray(response);
            if (wordsCount == 0) wordsCount = jsonResponse.length();
                for (int i = 0; i < wordsCount; i++) {
                    responseList.add(jsonResponse.getJSONObject(i).getString(entity));
            }
            Log.d(TAG, "processResponse: response processed. Response is: " + responseList);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "processResponse: Cannot parse response.Response is: "+ response +" Error message is: ", e);
        }
        processedResult.addAll(responseList);
    }
}