package br.com.fbs.popularmovies.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import br.com.fbs.popularmovies.model.FavoriteMovie;

/**
 * Created by felipe on 15/11/18.
 */

@Database(entities = {FavoriteMovie.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class, "popularmovies")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract FavoriteMovieDao movieDao();
}
