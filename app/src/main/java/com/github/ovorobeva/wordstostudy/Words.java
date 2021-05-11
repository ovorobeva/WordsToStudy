package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

public class Words {

    private static final String WORD = "word";
    private static final String PART_OF_SPEECH = "partOfSpeech";

    public static void requestRandomWord(Context context, String url, String entity, int wordsCount, List<String> callback) {

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<String> words = new LinkedList<>();
                            int privateCount = wordsCount;
                            JSONArray jsonResponse = new JSONArray(response);

                            if (privateCount == 0) privateCount = jsonResponse.length();
                            for (int i = 0; i < privateCount; i++) {
                                words.add(jsonResponse.getJSONObject(i).getString(entity));
                                //addValueToResponseArray(jsonResponse.getJSONObject(i).getString(value));
                            }
                            callback.clear();
                            callback.addAll(words);
                            Log.d(AppWidget.class.getCanonicalName() + ".requestRandomWord", "Response is received. The word is: " + words);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(AppWidget.class.getCanonicalName() + ".requestRandomWord", "Response is incorrect, new word is undefined");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(AppWidget.class.getCanonicalName() + ".requestRandomWord", "Something went wrong during request");
            }
        });
        queue.add(stringRequest);
    }

    public static void getWords(Context context, List<String> words) {

        int wordsCount = ConfigureActivity.loadWordsCountFromPref(context);

        boolean isHasDictionaryDef = true;
        String includePartOfSpeech = "noun%2Cadjective%2Cverb%2Cadverb%2Cidiom%2Cpast-participle";
        String excludePartOfSpeech = "interjection%2Cpronoun%2Cpreposition%2Cabbreviation%2Caffix%2Carticle" +
                "%2Cauxiliary-verb%2Cconjunction%2Cdefinite-article%2Cfamily-name%2Cgiven-name%2Cimperative%2" +
                "Cproper-noun%2Cproper-noun-plural%2Csuffix%2Cverb-intransitive%2Cverb-transitive";
        //todo: to make constants for beginner, intermediate, advanced
        String minCorpusCount="100000";
        String maxCorpusCount="-1";
        String minDictionaryCount="0";
        String maxDictionaryCount="-1";
        String minLength="2";
        String maxLength="-1";
        String limit=String.valueOf(wordsCount);
        String api_key="55k0ykdy6pe8fmu69pwjk94es02i9085k3h1hn11ku56c4qep";

        String url = "https://api.wordnik.com/v4/words.json/randomWords?hasDictionaryDef="+ isHasDictionaryDef +
                "&includePartOfSpeech=" + includePartOfSpeech +
                "&excludePartOfSpeech=" + excludePartOfSpeech +
                "&minCorpusCount=" + minCorpusCount +
                "&maxCorpusCount=" + maxCorpusCount +
                "&minDictionaryCount=" + minDictionaryCount +
                "&maxDictionaryCount=" + maxDictionaryCount +
                "&minLength=" + minLength +
                "&maxLength=" + maxLength +
                "&limit=" + limit +
                "&api_key=" + api_key;

        requestRandomWord(context, url, WORD, wordsCount, words);
    }
}

