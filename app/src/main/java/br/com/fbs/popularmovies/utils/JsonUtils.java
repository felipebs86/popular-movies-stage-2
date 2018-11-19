package br.com.fbs.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.fbs.popularmovies.dto.MovieDto;

/**
 * Created by felipe on 19/11/18.
 */

public class JsonUtils {

    public static List<MovieDto> getMoviesDataFromJson(String receiptJson) throws JSONException {
        JSONObject moviesJson = new JSONObject(receiptJson);
        JSONArray resultsArray = moviesJson.getJSONArray("results");

        List<MovieDto> movieDtos = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            MovieDto movieDto = new MovieDto();

            JSONObject movieInfo = resultsArray.getJSONObject(i);

            movieDto.setId(movieInfo.optString("id"));
            movieDto.setTitle(movieInfo.optString("title"));
            movieDto.setPosterPath(movieInfo.optString("poster_path"));
            movieDto.setSynopsis(movieInfo.optString("overview"));
            movieDto.setVoteAverage(movieInfo.optDouble("vote_average"));
            movieDto.setReleaseDate(movieInfo.optString("release_date"));
            movieDtos.add(movieDto);
        }

        return movieDtos;
    }
}
