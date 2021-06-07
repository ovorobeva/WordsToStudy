package com.github.ovorobeva.wordstostudy;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordsClient {
    private static final String TAG = "Custom logs";
    WordsApi wordsApi;
    private final String BASE_URL = "https://api.wordnik.com/v4/";
    private final String API_KEY = "55k0ykdy6pe8fmu69pwjk94es02i9085k3h1hn11ku56c4qep";

    private final String WORD = "word";
    private final String PART_OF_SPEECH = "partOfSpeech";

    public WordsClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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

        List<String> includePartOfSpeech = new ArrayList<>();
        includePartOfSpeech.add("noun");
        includePartOfSpeech.add("adjective");
        includePartOfSpeech.add("verb");
        includePartOfSpeech.add("idiom");
        includePartOfSpeech.add("past-participle");

        List<String> excludePartOfSpeech = new ArrayList<>();
        excludePartOfSpeech.add("interjection");
        excludePartOfSpeech.add("pronoun");
        excludePartOfSpeech.add("preposition");
        excludePartOfSpeech.add("abbreviation");
        excludePartOfSpeech.add("affix");
        excludePartOfSpeech.add("article");
        excludePartOfSpeech.add("auxiliary-verb");
        excludePartOfSpeech.add("conjunction");
        excludePartOfSpeech.add("definite-article");
        excludePartOfSpeech.add("family-name");
        excludePartOfSpeech.add("given-name");
        excludePartOfSpeech.add("imperative");
        excludePartOfSpeech.add("proper-noun");
        excludePartOfSpeech.add("proper-noun-plural");
        excludePartOfSpeech.add("suffix");
        excludePartOfSpeech.add("verb-intransitive");
        excludePartOfSpeech.add("verb-transitive");

        Call<JSONArray> randomWords = wordsApi.sendRequest(true, includePartOfSpeech, excludePartOfSpeech, apiVariables.get("minCorpusCount"),
                apiVariables.get("maxCorpusCount"), apiVariables.get("minDictionaryCount"), apiVariables.get("maxDictionaryCount"),
                apiVariables.get("minLength"), apiVariables.get("maxLength"), String.valueOf(wordsCount), API_KEY);

/*        try {
            Response<JSONArray> response = randomWords.execute();
            Log.d(TAG, "getRandomWords: " + response.body());
        } catch (IOException e) {
            Log.d(TAG, "getPartsOfSpeech: Something went wrong during request");
            e.printStackTrace();
        }*/

        randomWords.enqueue(new Callback<JSONArray>() {
            @Override
            public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                if (response.isSuccessful()) {
                    processResponse(wordsCount, WORD, response.body(), processedResult);
                    Log.d(TAG, "getRandomWords: " + response.body());
                } else {
                    Log.e(TAG, "getRandomWords: There is an error during request. Response code is: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<JSONArray> call, Throwable t) {
                Log.d(TAG, "getPartsOfSpeech: Something went wrong during request");
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


        Call<JSONArray> partsOfSpeech = wordsApi.sendRequest(word, apiVariables.get("includeRelated"),
                apiVariables.get("useCanonical"),apiVariables.get("includeTags"), LIMIT, API_KEY);


        Response<JSONArray> response = null;
        try {
            response = partsOfSpeech.execute();
            Log.d(TAG, "getPartsOfSpeech: " + response.body());
            //todo: to migrate this into Words.class or to process request here
        } catch (IOException e) {
            Log.d(TAG, "getPartsOfSpeech: Something went wrong during request");
            e.printStackTrace();
        }
        processResponse(0, PART_OF_SPEECH, response.body(), processedResult);
        return processedResult;

    }

    private void processResponse(int wordsCount, String entity, JSONArray response, List<String> processedResult){
        List<String> responseList = new LinkedList<>();
        try {
            if (wordsCount == 0) wordsCount = response.length();
            for (int i = 0; i < wordsCount; i++) {
                responseList.add(response.getJSONObject(i).getString(entity));
            }
            Log.d(TAG, "processResponse: response processed. Response is: " + responseList);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "processResponse: Cannot parse response. Error message is: ", e);
        }
        processedResult.addAll(responseList);
    }
}