package br.com.fbs.popularmovies;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import br.com.fbs.popularmovies.data.AppDatabase;
import br.com.fbs.popularmovies.dto.MovieDto;
import br.com.fbs.popularmovies.model.FavoriteMovie;
import br.com.fbs.popularmovies.utils.NetworkUtils;
import br.com.fbs.popularmovies.utils.ThreadExecutor;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private List<MovieDto> moviesDataFromJson;
    private GridView mGridView;
    private ProgressBar progressBarLoading;
    private TextView textViewError;
    private AppDatabase mDatabase;
    private Executor executor;

    private List<FavoriteMovie> favoriteMovies;

    private static final int QUERY_LOADER = 86;
    private static final String FILM_QUERY = "FILM_QUERY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = findViewById(R.id.gv_movies);
        progressBarLoading = findViewById(R.id.pb_loading);
        textViewError = findViewById(R.id.tv_error_message);
        favoriteMovies = new ArrayList<FavoriteMovie>();

        mDatabase = AppDatabase.getDatabase(this);
        executor = new ThreadExecutor();
        
        setupViewModel();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_sort),
                Context.MODE_PRIVATE);
        String preferenceSort = sharedPreferences.getString(getString(R.string.preference_sort), getString(R.string.sort_popular));

        makeFilmQuery(preferenceSort);

        mGridView.setOnItemClickListener(movieClickListener);

    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getFavoriteMovies().observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovie> favorites) {
                if (favorites.size() > 0) {
                    favoriteMovies.clear();
                    favoriteMovies = favorites;

                }
                //makeFilmQuery("favorites");
            }
        });
    }

    private void clearFavoriteList() {
        if (favoriteMovies != null) {
            favoriteMovies.clear();
        } else {
            favoriteMovies = new ArrayList<FavoriteMovie>();
        }
    }

    private final GridView.OnItemClickListener movieClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MovieDto movieDto = (MovieDto) parent.getItemAtPosition(position);

            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra("movieDetails", movieDto);
            startActivity(intent);
        }
    };

    private void makeFilmQuery(final String sort) {
        URL searchUrl = NetworkUtils.buildUrlForFilms(sort);
        if (!hasConecction()) {
            View view = findViewById(R.id.gv_movies);
            Snackbar snackbar = Snackbar.make(view, getString(R.string.error_message), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Recarregar", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeFilmQuery(sort);
                }
            });
            snackbar.show();
            return;
        }

        Bundle queryBundle = new Bundle();
        queryBundle.putString(FILM_QUERY, searchUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(QUERY_LOADER);
        if (loader == null) {
            loaderManager.initLoader(QUERY_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(QUERY_LOADER, queryBundle, this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_sort),
                Context.MODE_PRIVATE);

        if (id == R.id.action_top_rated) {
            String endpoint = "top_rated";
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.preference_sort), endpoint);
            editor.apply();
            makeFilmQuery(endpoint);
        }

        if (id == R.id.action_most_popular) {
            String endpoint = "popular";
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.preference_sort), endpoint);
            editor.apply();
            makeFilmQuery(endpoint);
        }

        if (id == R.id.action_display_favorites) {
            displayFavorites();
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayFavorites() {
        List<MovieDto> movieDtos = new ArrayList<>();
        for (FavoriteMovie favoriteMovie : favoriteMovies) {
            Log.i("WTF", favoriteMovie.posterPath);
            MovieDto teste = MovieDto.MovieDtoFrom(favoriteMovie);
            Log.i("WTF", teste.getPosterPath());
            movieDtos.add(MovieDto.MovieDtoFrom(favoriteMovie));
        }

        mGridView.setAdapter(new ImageAdapter(MainActivity.this, movieDtos));
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                Log.i("PopularMovies", "Iniciando carregamento no background");
                if (args == null) {
                    return;
                }
                progressBarLoading.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String searchUrl = args.getString(FILM_QUERY);
                if (searchUrl == null || TextUtils.isEmpty(searchUrl)) {
                    return null;
                }
                try {
                    URL urlForFilms = new URL(searchUrl);
                    return NetworkUtils.getResponseFromHttpUrl(urlForFilms);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.i("PopularMovies", "Finalizando carregamento background");
        progressBarLoading.setVisibility(View.INVISIBLE);
        if (data != null && !data.equals("")) {
            showGridFilms();
            try {
                moviesDataFromJson = getMoviesDataFromJson(data);
                mGridView.setAdapter(new ImageAdapter(MainActivity.this, moviesDataFromJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private List<MovieDto> getMoviesDataFromJson(String receiptJson) throws JSONException {
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

    private void showGridFilms() {
        textViewError.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mGridView.setVisibility(View.INVISIBLE);
        textViewError.setVisibility(View.VISIBLE);
    }
}
