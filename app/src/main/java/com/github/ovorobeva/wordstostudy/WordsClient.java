package com.github.ovorobeva.wordstostudy;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordsClient {
    private static final Object OBJECT = new Object();
    private static final String TAG = "Custom logs";
    private static WordsClient wordsClient;
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


    public synchronized void getWords(List<GeneratedWords> responseBody, int wordsCount, Context context) {

        Log.d(TAG, "getWords: creating request: 3");
        Call<List<GeneratedWords>> getWordsRequest = wordsApi.sendRequest();

        Log.d(TAG, "getWords: sending request: 4");
        ///////////








       ////////////////
        getWordsRequest.enqueue(new Callback<List<GeneratedWords>>() {
            @Override
            public synchronized void onResponse(Call<List<GeneratedWords>> call, Response<List<GeneratedWords>> response) {
                Log.d(TAG, "onResponse: saving results of response : 5");
                if (response.isSuccessful()) {
                    responseBody.addAll(response.body());
                    Log.d(TAG, "onResponse: Response by the url " + response.raw().request().url() + " is received.\nResponse to process is: " + responseBody);



                    if (responseBody.isEmpty()) return;
                    Random random = new Random();
                    List<GeneratedWords> randomWords = new LinkedList<>();
                    int id;
                    for (int i = 0; i < wordsCount; i++){
                        id = random.nextInt(responseBody.size());
                        if (id > 0) id--;
                        randomWords.add(responseBody.get(id));
                    }
                    Log.d(TAG, "onResponse: random words are: " + randomWords);

                    List<String> processedResult = new LinkedList<>();

                    for (GeneratedWords generatedWord : randomWords) {
                        processedResult.add(generatedWord.getEn() + " - " + generatedWord.getRu());
                    }


                    StringBuilder words = new StringBuilder();

                    for (String s : processedResult) {
                        words.append(s).append("\n");
                    }
                    Log.d(TAG, "onResponse: words are: " + words);


                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                    views.setTextViewText(R.id.appwidget_text, words);

                    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    final ComponentName cn = new ComponentName(context, AppWidget.class);
                    mgr.updateAppWidget(cn, views);






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