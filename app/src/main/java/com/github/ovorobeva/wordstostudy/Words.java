package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.util.Log;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.github.ovorobeva.wordstostudy.Integration.getResponseArray;
import static com.github.ovorobeva.wordstostudy.Integration.sendRequest;

public class Words {
    private static final String WORD = "word";
    private static final String PART_OF_SPEECH = "partOfSpeech";
    private List<String> words;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public Words(Context context) {
        setWords(getWords(context));
    }

    private List<String> getRandomWords(Context context, int wordsCount){
        List<String> words;
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
        //
        words = sendRequest(context, url, wordsCount, WORD);

        Log.d("Custom logs", "getRandomWords: " + words);
        return words;
    }

    private List<String> getPartOfSpeech(Context context, String word){
        List<String> partsOfSpeech;
        String limit = "500";
        boolean includeRelated = false;
        boolean useCanonical = false;
        boolean includeTags = false;
        String api_key = "55k0ykdy6pe8fmu69pwjk94es02i9085k3h1hn11ku56c4qep";

        String url = "https://api.wordnik.com/v4/word.json/" + word + "/definitions?includeRelated=" + includeRelated +
                "&useCanonical=" + useCanonical +
                "&includeTags=" + includeTags +
                "&limit=" + limit +
                "&api_key=" + api_key;
        Log.d("URL", url);
        //
        partsOfSpeech = sendRequest(context, url, 0, PART_OF_SPEECH);

        Log.d("Custom logs", "getPartOfSpeech: " + partsOfSpeech);
        return partsOfSpeech;
    }
    private List<String> getWords(Context context) {
        int wordsCount;
        wordsCount = ConfigureActivity.loadWordsCountFromPref(context);
        List <String> words;
        List <String> partsOfSpeech = new LinkedList<>();
        words = getRandomWords(context, wordsCount);
        Iterator<String> iterator = words.iterator();
        int removedCounter = 0;
        Log.d("Custom logs", "getWords: Starting looking for parts of speech for words: \n" + words);

        while (iterator.hasNext()){
            String word = iterator.next();
            partsOfSpeech.clear();
            partsOfSpeech = getPartOfSpeech(context, word.toLowerCase());
            Log.d("Custom logs", "getWords: Parts of speech for a word " + word + " are:" + partsOfSpeech);
            for (String parOfSpeech: partsOfSpeech){
                if (!(parOfSpeech.equals("noun") || parOfSpeech.equals("adjective") || parOfSpeech.equals("adverb")
                        || parOfSpeech.equals("idiom") || parOfSpeech.equals("past-participle"))){
                    removedCounter++;
                    Log.d("Custom logs", "getWords: Removing the word " + word + " because of part of speech " + parOfSpeech + ". The count of deleted words is " + removedCounter);
                    iterator.remove();
                    break;
                }
            }
        }
        Log.d("TAG", "getWords: " + words);
        return words;
    }
}

