package com.github.ovorobeva.wordstostudy;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.ovorobeva.wordstostudy.Preferences.saveWordsToPref;

public class WordsClient {
    private static final Object OBJECT = new Object();
    private static final String TAG = "Custom logs";
    private static WordsClient wordsClient;
    private final String BASE_URL = "http://130.61.252.79:8080/";
    private final String BASE_URL_RESERVE = "https://raw.githubusercontent.com/ovorobeva/WordsParser/master/src/main/resources/";

    private WordsClient() {
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


    public void getWords(int wordsCount, Context context, AppWidgetManager appWidgetManager, RemoteViews views) {
        final VocabularyWordsAPI wordsApi;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        builder.client(httpClient.build());
        Retrofit retrofit = builder.build();

        wordsApi = retrofit.create(VocabularyWordsAPI.class);
        List<GeneratedWords> responseBody = new ArrayList<>();
        try {
            Call<List<GeneratedWords>> getWordsRequest = wordsApi.sendRequest(wordsCount);
            getWordsRequest.enqueue((new Callback<List<GeneratedWords>>() {
                @Override
                public void onResponse(Call<List<GeneratedWords>> call, Response<List<GeneratedWords>> response) {
                    if (response.isSuccessful()) {
                        responseBody.addAll(response.body());
                        Log.d(TAG, "onResponse: Response by the url " + response.raw().request().url() + " is received.\nResponse to process is: " + responseBody);
                        setWords(responseBody, views, context);

                        final ComponentName cn = new ComponentName(context, AppWidget.class);
                        appWidgetManager.updateAppWidget(cn, views);
                    } else
                        Log.e(TAG, "onResponse: There is an error during request. Response code is: " + response.code() + " url is: " + response.raw().request().url());
                }

                @Override
                public void onFailure(Call<List<GeneratedWords>> call, Throwable t) {
                    Log.e(TAG, "onFailure: Something went wrong during request by url " + call.request().url() + "\n Error is: " + t.getMessage(), t);
                    t.printStackTrace();
                    getWordsReserve(wordsCount, context, appWidgetManager, views);
                }
            }));
        } catch (Exception e) {
            Log.d(TAG, "getWords: exception: " + e);
            getWordsReserve(wordsCount, context, appWidgetManager, views);
        }
    }

    public void getWordsReserve(int wordsCount, Context context, AppWidgetManager appWidgetManager, RemoteViews views) {
        List<GeneratedWords> responseBody = new ArrayList<>();

        final WordsApi wordsApi;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_RESERVE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        wordsApi = retrofit.create(WordsApi.class);

        Call<List<GeneratedWords>> getWordsRequest = wordsApi.sendRequest();
        getWordsRequest.enqueue(new Callback<List<GeneratedWords>>() {
            @Override
            public synchronized void onResponse(Call<List<GeneratedWords>> call, Response<List<GeneratedWords>> response) {

                if (response.isSuccessful()) {
                    responseBody.addAll(response.body());
                    Log.d(TAG, "onResponse: Response by the url " + response.raw().request().url() + " is received.\nResponse to process is: " + responseBody);


                    if (responseBody.isEmpty()) return;
                    Random random = new Random();
                    List<GeneratedWords> randomWords = new LinkedList<>();
                    int id;
                    for (int i = 0; i < wordsCount; i++) {
                        id = random.nextInt(responseBody.size());
                        if (id > 0) id--;
                        randomWords.add(responseBody.get(id));
                    }
                    Log.d(TAG, "onResponse: random words are: " + randomWords);
                    setWords(randomWords, views, context);

                    final ComponentName cn = new ComponentName(context, AppWidget.class);
                    appWidgetManager.updateAppWidget(cn, views);
                } else
                    Log.e(TAG, "onResponse: There is an error during request. Response code is: " + response.code() + " url is: " + response.raw().request().url());
            }

            @Override
            public void onFailure(Call<List<GeneratedWords>> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong during request by url " + call.request().url() + "\n Error is: " + t.getMessage(), t);
                t.printStackTrace();
            }
        });
    }

    private void setWords(List<GeneratedWords> randomWords, RemoteViews views, Context context){
        List<String> processedResult = new LinkedList<>();

        for (GeneratedWords generatedWord : randomWords) {
            processedResult.add(generatedWord.getEn() + " - " + generatedWord.getRu());
        }

        StringBuilder words = new StringBuilder();

        for (String s : processedResult) {
            words.append(s).append("\n");
        }
        Log.d(TAG, "onResponse: words are: " + words);

        views.setTextViewText(R.id.words_edit_text, words);
        saveWordsToPref(words.toString(), context);
    }
}