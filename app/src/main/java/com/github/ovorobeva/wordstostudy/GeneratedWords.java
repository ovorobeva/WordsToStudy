package com.github.ovorobeva.wordstostudy;

public class GeneratedWords {

    private Integer id;
    private String en;
    private String ru;

    public GeneratedWords() {
    }

    public GeneratedWords(Integer id, String en) {
        this.id = id;
        this.en = en.toLowerCase();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru.toLowerCase();
    }

    public String getEn() {
        return en;
    }

    public void setEn(String word) {
        this.en = word;
    }

    @Override
    public String toString() {
        return "GeneratedWords{" +
                "id=" + id +
                ", en='" + en + '\'' +
                ", ru='" + ru + '\'' +
                '}';
    }
}


