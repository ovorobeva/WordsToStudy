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

import javax.security.auth.callback.Callback;

public class Integration {
    private static final String WORD = "word";
    static private LinkedList<String> responseArray = new LinkedList<>();

    public static void addValueToResponseArray(String value) {
        responseArray.add(value);
    }

    public static LinkedList<String> getResponseArray() {
        return responseArray;
    }

    public static void sendRequest(Context context, String url, int count, String value, List<String> callback) {
        //public static ArrayList<String> requestRandomWord(Context context, int wordsCount) {


// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        //String url = "https://random-words-api.vercel.app/word";
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<String> array = new LinkedList<>();
                        try {
                            int privateCount = count;
                            JSONArray jsonResponse = new JSONArray(response);
                            //responseArray.clear();
                            if (privateCount == 0) privateCount = jsonResponse.length();
                            for (int i = 0; i < privateCount; i++) {
                                array.add(jsonResponse.getJSONObject(i).getString(value));
                                //addValueToResponseArray(jsonResponse.getJSONObject(i).getString(value));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.addAll(array);
                        Log.d(this.getClass().getCanonicalName() + ".requestRandomWord", "Response is received: " + callback);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp", "onResponse: " + error.getMessage());
                responseArray.clear();
                addValueToResponseArray(error.getMessage());
                callback.addAll(responseArray);
                Log.d(AppWidget.class.getCanonicalName() + ".requestRandomWord", "Something went wrong during request: " + callback);
            }
        });
        queue.add(stringRequest);
    //    return responseArray;
    }
}

