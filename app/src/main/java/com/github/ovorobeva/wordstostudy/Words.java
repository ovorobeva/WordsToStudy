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

public class Words {
    private static String words;

    public static String requestRandomWord(Context context) {

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
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
        String limit=String.valueOf(ConfigureActivity.loadWordsCountFromPref(context));
        String api_key="YOURAPIKEY";

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
        //String url = "https://random-words-api.vercel.app/word";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            words = new JSONArray(response).getJSONObject(0).getString("word");
                            Log.d(AppWidget.class.getCanonicalName() + ".requestRandomWord", "Response is received. The word is: " + words);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            words = "undefined";
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
        return words;
    }

    public static String getWords(Context context) {
        words = requestRandomWord(context);
        return words;
    }
}

