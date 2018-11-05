package br.com.fbs.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.com.fbs.popularmovies.BuildConfig;

/**
 * Created by felipe on 20/09/18.
 */

public class NetworkUtils {
    private final static String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String SUFFIX_TRAILERS = "/videos";
    private final static String SUFFIX_REVIEWS = "/reviews";
    private final static String PARAM_LANGUAGE = "language";
    private final static String LANGUAGE_PT_BR = "pt-BR";
    private final static String LANGUAGE_EN_US = "en-US";
    private final static String PARAM_KEY = "api_key";
    private final static String API_KEY = BuildConfig.API_KEY;

    public static URL buildUrlForFilms(String query) {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL + query).buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE_PT_BR)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlForTrailers(String query) {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL + query + SUFFIX_TRAILERS).buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE_EN_US)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlForReviews(String query) {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL + query + SUFFIX_REVIEWS).buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE_PT_BR)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
