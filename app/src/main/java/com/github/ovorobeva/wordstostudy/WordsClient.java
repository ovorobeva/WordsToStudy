package com.github.ovorobeva.wordstostudy;

import static com.github.ovorobeva.wordstostudy.AppWidget.setTextStyle;
import static com.github.ovorobeva.wordstostudy.Preferences.loadWordsFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.saveWordsToPref;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordsClient {
    private static final Object OBJECT = new Object();
    private static final String TAG = "Custom logs";
    private static WordsClient wordsClient;

    private final AppWidgetManager appWidgetManager;
    private final RemoteViews views;
    private final Context context;

    private WordsClient(Context context, AppWidgetManager appWidgetManager, RemoteViews views) {
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.views = views;
    }

    public static WordsClient getWordsClient(Context context, AppWidgetManager appWidgetManager, RemoteViews views) {
        if (wordsClient != null)
            return wordsClient;

        synchronized (OBJECT) {
            if (wordsClient == null)
                wordsClient = new WordsClient(context, appWidgetManager, views);
            return wordsClient;
        }
    }

    public synchronized void getWords(int wordsCount, boolean isAdditional) {
        Log.d(TAG, "getWords: Getting words from API...");
        final VocabularyWordsAPI wordsApi;

        String BASE_URL = "http://130.61.252.79:8080/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        wordsApi = retrofit.create(VocabularyWordsAPI.class);

        List<GeneratedWords> responseBody = new ArrayList<>();
        try {
            Call<List<GeneratedWords>> getWordsRequest = wordsApi.sendRequest(wordsCount);
            Log.d(TAG, "getWords: Sending request " + getWordsRequest.request());
            getWordsRequest.enqueue((new Callback<List<GeneratedWords>>() {
                @Override
                public synchronized void onResponse(Call<List<GeneratedWords>> call, Response<List<GeneratedWords>> response) {
                    Log.d(TAG, "onResponse: getting response...");
                    if (response.isSuccessful()) {
                        responseBody.addAll(response.body());
                        Log.d(TAG, "onResponse: Response by the url " + response.raw().request().url() + " is received.\nResponse to process is: " + responseBody);
                        setWords(responseBody, views, context, isAdditional, appWidgetManager);

                        final ComponentName cn = new ComponentName(context, AppWidget.class);
                        appWidgetManager.updateAppWidget(cn, views);
                    } else {
                        Log.e(TAG, "onResponse: There is an error during request. Response code is: " + response.code() + " url is: " + response.raw().request().url());
                        getWordsReserve(wordsCount, isAdditional);
                }}

                @Override
                public synchronized void onFailure(Call<List<GeneratedWords>> call, Throwable t) {
                    if (t.getClass().equals(ConnectException.class)) {
                        Log.e(TAG, "onFailure: Something is wrong with the internet connection: \n" + t.getMessage());
                        retryGettingWords(wordsCount, isAdditional);
                    } else {
                        Log.e(TAG, "onFailure: Something went wrong during request by url " + call.request().url() + "\n Error is: " + t.getMessage(), t);
                        getWordsReserve(wordsCount, isAdditional);
                    }
                }
            }));
        } catch (Exception e) {
            Log.d(TAG, "getWords: exception: " + e);
            getWordsReserve(wordsCount, isAdditional);
        }
    }

    public synchronized void getWordsReserve(int wordsCount, boolean isAdditional) {
        List<GeneratedWords> responseBody = new ArrayList<>();

        final WordsApi wordsApi;

        String BASE_URL_RESERVE = "https://raw.githubusercontent.com/ovorobeva/WordsParser/master/src/main/resources/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_RESERVE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        wordsApi = retrofit.create(WordsApi.class);

        Call<List<GeneratedWords>> getWordsRequest = wordsApi.sendRequest();
        Log.d(TAG, "getWords: Sending reserved request " + getWordsRequest.request());
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
                    setWords(randomWords, views, context, isAdditional, appWidgetManager);

                    final ComponentName cn = new ComponentName(context, AppWidget.class);
                    appWidgetManager.updateAppWidget(cn, views);
                } else
                    Log.e(TAG, "onResponse: There is an error during request. Response code is: " + response.code() + " url is: " + response.raw().request().url());
            }

            @Override
            public synchronized void onFailure(Call<List<GeneratedWords>> call, Throwable t) {
                if (t.getClass().equals(ConnectException.class) || t.getClass().equals(SocketTimeoutException.class) || t.getClass().equals(UnknownHostException.class)) {
                    Log.e(TAG, "onFailure: Something is wrong with the internet connection: \n" + t.getMessage());
                } else {
                    Log.e(TAG, "onFailure: Something went wrong during request by url " + call.request().url() + "\n Error is: " + t.getMessage(), t);
                    t.printStackTrace();
                }
                retryGettingWords(wordsCount, isAdditional);
            }
        });
    }

    private synchronized void setWords(List<GeneratedWords> randomWords, RemoteViews views, Context context, boolean isAdditional, AppWidgetManager manager) {
        List<String> processedResult = new LinkedList<>();

        for (GeneratedWords generatedWord : randomWords) {
            processedResult.add(generatedWord.getEn() + " - " + generatedWord.getRu());
        }

        StringBuilder words = new StringBuilder();
        if (isAdditional) words.append(loadWordsFromPref(context));

        for (String s : processedResult) {
            words.append(s).append("\n");
        }
        Log.d(TAG, "onResponse: words are: " + words);

        views.setTextViewText(R.id.words_edit_text, words);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
        for (int id : ids) {
            setTextStyle(views, id, context);
        }
        saveWordsToPref(words.toString(), context);
    }

    private synchronized void retryGettingWords(int wordsCount, boolean isAdditional){
        try {
            Thread.sleep(10000);
            getWords(wordsCount, isAdditional);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}