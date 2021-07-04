package com.github.ovorobeva.wordstostudy;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordsClient {
    private static final Object OBJECT = new Object();
    private static final String TAG = "Custom logs";
    private volatile static WordsClient wordsClient;
    private final String BASE_URL = "https://raw.githubusercontent.com/ovorobeva/WordsParser/master/src/main/resources/";
    private final WordsApi wordsApi;

    private WordsClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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


    public void getWords(List<GeneratedWords> responseBody) {

        Call<List<GeneratedWords>> getWordsRequest = wordsApi.sendRequest();
        getWordsRequest.enqueue(new Callback<List<GeneratedWords>>() {
            @Override
            public void onResponse(Call<List<GeneratedWords>> call, Response<List<GeneratedWords>> response) {
                if (response.isSuccessful()) {
                    responseBody.addAll(response.body());
                    Log.d(TAG, "onResponse: Response by the url " + response.raw().request().url() + " is received.\nResponse to process is: " + responseBody);

                } else
                    Log.e(TAG, "onResponse: There is an error during request. Response code is: " + response.code() + " url is: " + response.raw().request().url());

            }

            @Override
            public void onFailure(Call<List<GeneratedWords>> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong during request by url " + call.request().url(), t);
                t.printStackTrace();
            }
        });
    }


}