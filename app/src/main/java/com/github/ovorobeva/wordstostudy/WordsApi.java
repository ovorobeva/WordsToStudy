package com.github.ovorobeva.wordstostudy;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WordsApi {

    @GET("word.json/{word}/definitions")
    Call<JSONArray> sendRequest(@Path("word") String word,
                                @Query("includeRelated") boolean includeRelated,
                                @Query("useCanonical") boolean useCanonical,
                                @Query("includeTags") boolean includeTags,
                                @Query("limit") String limit,
                                @Query("apiKey") String apiKey);


    @GET("words.json/")
    Call<JSONArray> sendRequest(@Query("isHasDictionaryDef") boolean isHasDictionaryDef,
                                @Query("includePartOfSpeech") List<String> includePartOfSpeech,
                                @Query("excludePartOfSpeech") List<String> excludePartOfSpeech,
                                //todo: to make constants for beginner, intermediate, advanced
                                @Query("minCorpusCount") String minCorpusCount,
                                @Query("maxCorpusCount") String maxCorpusCount,
                                @Query("minDictionaryCount") String minDictionaryCount,
                                @Query("maxDictionaryCount") String maxDictionaryCount,
                                @Query("minLength") String minLength,
                                @Query("maxLength") String maxLength,
                                @Query("limit") String limit,
                                @Query("apiKey") String apiKey);
}
