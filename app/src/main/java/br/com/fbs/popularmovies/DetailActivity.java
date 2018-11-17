package br.com.fbs.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import br.com.fbs.popularmovies.data.AppDatabase;
import br.com.fbs.popularmovies.dto.MovieDto;
import br.com.fbs.popularmovies.dto.ReviewDto;
import br.com.fbs.popularmovies.dto.TrailerDto;
import br.com.fbs.popularmovies.model.FavoriteMovie;
import br.com.fbs.popularmovies.utils.NetworkUtils;
import br.com.fbs.popularmovies.utils.ThreadExecutor;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private final String INTENT_EXTRAS = "movieDetails";
    private MovieDto movieDto;
    private TextView title;
    private TextView releaseDate;
    private TextView voteAverage;
    private TextView synopsis;
    private ImageView poster;
    private ListView trailersList;
    private ListView reviewsList;
    private boolean isFavorited;
    private Button favoriteButton;

    private AppDatabase mDatabase;

    private Executor executor;

    private static final int QUERY_TRAILER_LOADER = 91;
    private static final String FILM_TRAILER_QUERY = "FILM_TRAILER_QUERY";
    private static final int QUERY_REVIEWS_LOADER = 10;
    private static final String FILM_REVIEWS_QUERY = "FILM_REVIEWS_QUERY";

    private static final String ENDPOINT_IMAGE = "https://image.tmdb.org/t/p/w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = findViewById(R.id.tv_detail_title);
        releaseDate = findViewById(R.id.tv_detail_release_date);
        voteAverage = findViewById(R.id.tv_vote_average);
        synopsis = findViewById(R.id.tv_detail_synopse);
        poster = findViewById(R.id.iv_detail_poster);
        trailersList = findViewById(R.id.lv_detail_trailers);
        reviewsList = findViewById(R.id.lv_detail_reviews);
        favoriteButton = findViewById(R.id.favorite_b);

        mDatabase = AppDatabase.getDatabase(this);
        executor = new ThreadExecutor();

        final Intent intent = getIntent();
        if (intent.hasExtra(INTENT_EXTRAS)) {
            movieDto = intent.getParcelableExtra(INTENT_EXTRAS);

            Log.i("PopularMovies", "Iniciando carregamento de detalhes do filme id: " + movieDto.getId());

            title.setText(movieDto.getTitle());
            releaseDate.setText(movieDto.getReleaseDate());
            voteAverage.setText(String.valueOf(movieDto.getVoteAverage()));
            synopsis.setText(movieDto.getSynopsis());

            Picasso.with(this)
                    .load(ENDPOINT_IMAGE + movieDto.getPosterPath())
                    .resize(185, 278)
                    .into(poster);

            isFavoriteFilm();

            makeDetailsQuery(movieDto.getId());
        }

        trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrailerDto trailerDto = (TrailerDto) parent.getItemAtPosition(position);
                Intent intentTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerDto.linkForTrailer()));
                getApplicationContext().startActivity(intentTrailer);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorited) {
                    Toast.makeText(DetailActivity.this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("PopularMovies", "Inserindo nos favoritos o filme id: " + movieDto.getId());
                            mDatabase.movieDao().insert(FavoriteMovie.favoriteMovieFrom(movieDto));
                        }
                    });
                    favoriteButton.setText(R.string.remove_to_favorite);
                    isFavorited = true;
                } else {
                    Toast.makeText(DetailActivity.this, "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("PopularMovies", "Deletando dos favoritos o filme id: " + movieDto.getId());
                            mDatabase.movieDao().delete(FavoriteMovie.favoriteMovieFrom(movieDto));
                        }
                    });
                    favoriteButton.setText(R.string.add_to_favorite);
                    isFavorited = false;
                }
            }
        });
    }

    private void isFavoriteFilm() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                FavoriteMovie favoriteMovie = mDatabase.movieDao().getFavoriteMovieById(movieDto.getId());

                if (favoriteMovie != null){
                    favoriteButton.setText(R.string.remove_to_favorite);
                    isFavorited = true;
                } else {
                    favoriteButton.setText(R.string.add_to_favorite);
                    isFavorited = false;
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                Log.i("PopularMovies", "Iniciando carregamento de detalhes no background");
                if (args == null) {
                    return;
                }
                forceLoad();
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String searchUrlTrailers = args.getString(FILM_TRAILER_QUERY);
                String searchUrlReviews = args.getString(FILM_REVIEWS_QUERY);

                if ((searchUrlTrailers == null || TextUtils.isEmpty(searchUrlTrailers)) &&
                        (searchUrlReviews == null || TextUtils.isEmpty(searchUrlReviews))) {
                    return null;
                }

                if (searchUrlTrailers != null && !TextUtils.isEmpty(searchUrlTrailers)) {
                    try {
                        URL urlForTrailers = new URL(searchUrlTrailers);
                        return NetworkUtils.getResponseFromHttpUrl(urlForTrailers);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                if (searchUrlReviews != null && !TextUtils.isEmpty(searchUrlReviews)) {
                    try {
                        URL urlForReviews = new URL(searchUrlReviews);
                        return NetworkUtils.getResponseFromHttpUrl(urlForReviews);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.i("PopularMovies", "Finalizando carregamento de detalhes no background");

        if (data != null && !data.equals("")) {
            if (loader.getId() == 91) {
                JSONObject trailersJson = null;
                try {
                    trailersJson = new JSONObject(data);
                    JSONArray resultsArray = trailersJson.getJSONArray("results");
                    List<TrailerDto> trailersDtoList = new ArrayList<>();

                    for (int i = 0; i < resultsArray.length(); i++) {
                        TrailerDto trailerDto = new TrailerDto();
                        JSONObject trailerInfo = resultsArray.getJSONObject(i);
                        trailerDto.setKey(trailerInfo.optString("key"));
                        trailerDto.setName(trailerInfo.optString("name"));
                        trailersDtoList.add(trailerDto);
                    }

                    ArrayAdapter<TrailerDto> trailerDtoArrayAdapter = new ArrayAdapter<TrailerDto>(getApplicationContext(), android.R.layout.simple_list_item_1, trailersDtoList);
                    trailersList.setAdapter(trailerDtoArrayAdapter);
                    setListViewHeightBasedOnItems(trailersList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (loader.getId() == 10) {
                JSONObject trailersJson = null;
                try {
                    trailersJson = new JSONObject(data);
                    JSONArray resultsArray = trailersJson.getJSONArray("results");
                    List<ReviewDto> reviewsDtoList = new ArrayList<>();

                    for (int i = 0; i < resultsArray.length(); i++) {
                        ReviewDto reviewDto = new ReviewDto();
                        JSONObject reviewInfo = resultsArray.getJSONObject(i);
                        reviewDto.setAuthor(reviewInfo.optString("author"));
                        reviewDto.setContent(reviewInfo.optString("content"));
                        reviewsDtoList.add(reviewDto);
                    }

                    ArrayAdapter<ReviewDto> reviewDtoArrayAdapter = new ArrayAdapter<ReviewDto>(getApplicationContext(), android.R.layout.simple_list_item_1, reviewsDtoList);
                    reviewsList.setAdapter(reviewDtoArrayAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private void makeDetailsQuery(final String id) {
        if (!hasConecction()) {
            View view = findViewById(R.id.gv_movies);
            Snackbar snackbar = Snackbar.make(view, getString(R.string.error_message), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Recarregar", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeDetailsQuery(id);
                }
            });
            snackbar.show();
            return;
        }

        Bundle queryTrailersBundle = new Bundle();
        queryTrailersBundle.putString(FILM_TRAILER_QUERY, NetworkUtils.buildUrlForTrailers(id).toString());

        Bundle queryReviewsBundle = new Bundle();
        queryReviewsBundle.putString(FILM_REVIEWS_QUERY, NetworkUtils.buildUrlForReviews(id).toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loaderTrailers = loaderManager.getLoader(QUERY_TRAILER_LOADER);
        Loader<String> loaderReviews = loaderManager.getLoader(QUERY_REVIEWS_LOADER);

        if (loaderTrailers == null) {
            loaderManager.initLoader(QUERY_TRAILER_LOADER, queryTrailersBundle, this);
        } else {
            loaderManager.restartLoader(QUERY_TRAILER_LOADER, queryTrailersBundle, this);
        }

        if (loaderReviews == null) {
            loaderManager.initLoader(QUERY_REVIEWS_LOADER, queryReviewsBundle, this);
        } else {
            loaderManager.restartLoader(QUERY_REVIEWS_LOADER, queryReviewsBundle, this);
        }
    }

    private boolean hasConecction() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        if (networkInfo == null) {return false;}
        if (!networkInfo.isConnected()) {return false;}
        if (!networkInfo.isAvailable()) {return false;}

        return true;
    }

    private static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}
