package br.com.fbs.popularmovies.dto;

/**
 * Created by felipe on 04/11/18.
 */

public class ReviewDto {
    String author;
    String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return author + ": " + content;
    }
}
