package br.com.fbs.popularmovies.dto;

/**
 * Created by felipe on 04/11/18.
 */

public class TrailerDto {
    String key;
    String name;
    private static final String YOUTUBE_ENDPOINT = "https://www.youtube.com/watch?v=";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String linkForTrailer() {
        return YOUTUBE_ENDPOINT + key;
    }

    @Override
    public String toString() {
        return name;
    }
}
