package com.github.ovorobeva.wordstostudy;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WordsClient {
    WordsApi wordsApi;
    final String API_KEY = "55k0ykdy6pe8fmu69pwjk94es02i9085k3h1hn11ku56c4qep";

    public WordsClient() {
        final String BASE_URL = "https://api.wordnik.com/v4/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        wordsApi = retrofit.create(WordsApi.class);
    }


    public JSONArray getRandomWords(int wordsCount) {
        Map<String, String> apiVariables = new HashMap<>();
        apiVariables.put("minCorpusCount", "100000");
        apiVariables.put("maxCorpusCount", "-1");
        apiVariables.put("minDictionaryCount", "0");
        apiVariables.put("maxDictionaryCount", "-1");
        apiVariables.put("minLength", "2");
        apiVariables.put("maxLength", "-1");
        apiVariables.put("limit", String.valueOf(wordsCount));

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
        excludePartOfSpeech.add("imperativ");
        excludePartOfSpeech.add("proper-noun");
        excludePartOfSpeech.add("proper-noun-plural");
        excludePartOfSpeech.add("suffix");
        excludePartOfSpeech.add("verb-intransitive");
        excludePartOfSpeech.add("verb-transitive");

        Call<JSONArray> randomWords = wordsApi.getRandomWords(true, includePartOfSpeech, excludePartOfSpeech, apiVariables.get("minCorpusCount"),
                apiVariables.get("maxCorpusCount"), apiVariables.get("minDictionaryCount"), apiVariables.get("maxDictionaryCount"),
                apiVariables.get("minLength"), apiVariables.get("maxLength"), apiVariables.get("limit"), API_KEY);

        Response<JSONArray> response = null;
        try {
            response = randomWords.execute();
            //todo: to migrate this into Words.class or to process request here
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body();

    }

    private JSONArray getPartsOfSpeech() {
        final String LIMIT = "500";
        Map<String, Boolean> apiVariables = new HashMap<>();
        apiVariables.put("includeRelated", false);
        apiVariables.put("useCanonical", false);
        apiVariables.put("includeTags", false);


        Call<JSONArray> partsOfSpeech = wordsApi.getPartOfSpeech(apiVariables.get("includeRelated"),
                apiVariables.get("useCanonical"),apiVariables.get("includeTags"), LIMIT, API_KEY);


        Response<JSONArray> response = null;
        try {
            response = partsOfSpeech.execute();
            //todo: to migrate this into Words.class or to process request here
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body();

    }
}