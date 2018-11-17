package br.com.fbs.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import br.com.fbs.popularmovies.model.FavoriteMovie;

/**
 * Created by felipe on 15/11/18.
 */

@Dao
public interface FavoriteMovieDao {
    @Query("SELECT * FROM FavoriteMovie")
    LiveData<List<FavoriteMovie>> getAll();

    @Query("SELECT * FROM FavoriteMovie WHERE movieId = :id")
    FavoriteMovie getFavoriteMovieById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteMovie favoriteMovie);

    @Delete
    void delete(FavoriteMovie favoriteMovie);
}
