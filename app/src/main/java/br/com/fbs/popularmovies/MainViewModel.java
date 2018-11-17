package br.com.fbs.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import br.com.fbs.popularmovies.data.AppDatabase;
import br.com.fbs.popularmovies.model.FavoriteMovie;

/**
 * Created by felipe on 17/11/18.
 */

public class MainViewModel extends AndroidViewModel{
    private LiveData<List<FavoriteMovie>> favoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getDatabase(this.getApplication());
        favoriteMovies = appDatabase.movieDao().getAll();
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }
}
