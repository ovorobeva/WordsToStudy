package com.github.ovorobeva.wordstostudy;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VocabularyWordsAPI {


    @GET("getwords/{count}")
    Call<List<GeneratedWords>> sendRequest(@Path("count") int count);
}
