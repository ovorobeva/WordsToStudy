package com.github.ovorobeva.wordstostudy;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WordsApi {


    @GET("words_source_v0.json")
    Call<List<GeneratedWords>> sendRequest();
}
