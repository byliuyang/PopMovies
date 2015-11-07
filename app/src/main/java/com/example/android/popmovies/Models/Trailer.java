package com.example.android.popmovies.Models;

/**
 * Created by harryliu on 10/26/15.
 */
public class Trailer {

    private String language;
    private String key;
    private String name;
    private String site;

    public Trailer(String key, String language, String name, String site) {
        this.key = key;
        this.language = language;
        this.name = name;
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
