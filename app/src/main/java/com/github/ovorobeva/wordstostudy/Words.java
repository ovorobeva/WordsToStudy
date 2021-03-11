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

import java.util.ArrayList;
import java.util.List;

public class Words {
    private static String resp;

    public static String requestRandomWord(Context context, int wordsCount) {

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        boolean isHasDictionaryDef = true;
        String includePartOfSpeech = "noun%2Cadjective%2Cverb%2Cadverb%2Cidiom%2Cpast-participle";
        String excludePartOfSpeech = "interjection%2Cpronoun%2Cpreposition%2Cabbreviation%2Caffix%2Carticle" +
                "%2Cauxiliary-verb%2Cconjunction%2Cdefinite-article%2Cfamily-name%2Cgiven-name%2Cimperative%2" +
                "Cproper-noun%2Cproper-noun-plural%2Csuffix%2Cverb-intransitive%2Cverb-transitive";
        //todo: to make constants for beginner, intermediate, advanced
        String minCorpusCount = "100000";
        String maxCorpusCount = "-1";
        String minDictionaryCount = "0";
        String maxDictionaryCount = "-1";
        String minLength = "2";
        String maxLength = "-1";
        String limit = String.valueOf(wordsCount);
        String api_key = "55k0ykdy6pe8fmu69pwjk94es02i9085k3h1hn11ku56c4qep";

        String url = "https://api.wordnik.com/v4/words.json/randomWords?hasDictionaryDef=" + isHasDictionaryDef +
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
        Log.d("URL", url);
        //String url = "https://random-words-api.vercel.app/word";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonResponse = new JSONArray(response);
                            resp = "";
                            for (int i = 0; i < wordsCount; i++) {
                                resp += jsonResponse.getJSONObject(i).getString("word") + "\n";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(AppWidget.class.getCanonicalName() + ".requestRandomWord", "Response is received: " + resp);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resp = error.getMessage();
                Log.d(AppWidget.class.getCanonicalName() + ".requestRandomWord", "Something went wrong during request: " + resp);
            }
        });
        queue.add(stringRequest);

        return resp;
    }

    public static ArrayList<String> getWords(Context context) {

        int wordsCount = 3;
        ArrayList<String> words = new ArrayList<>();

        wordsCount = ConfigureActivity.loadWordsCountFromPref(context);
        String response = requestRandomWord(context, wordsCount);
        JSONArray jsonResp;
        words.add(response);
        Log.d("TAG", "getWords: " + response);
/*        try {
            jsonResp = new JSONArray(response);
            words.add(jsonResp.getJSONObject(0).getString("word"));
            for (int i = 0; i < wordsCount; i++) {
                words.add(jsonResp.getJSONObject(i).getString("word"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return words;
    }
}

